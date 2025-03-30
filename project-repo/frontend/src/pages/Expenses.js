import React, { useState, useEffect } from 'react';
import API from '../api';
import { jwtDecode } from 'jwt-decode';
import { Modal, Button, Table, Form, Dropdown } from 'react-bootstrap';
import { FaPlus, FaSort, FaTrash, FaEdit } from 'react-icons/fa';

const Expenses = () => {
    const [expenses, setExpenses] = useState([]);
    const [selectedExpenses, setSelectedExpenses] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [newExpense, setNewExpense] = useState({
        category: '',
        payee: '',
        amount: '',
        currency: '',
        description: '',
    });
    const [isModifyMode, setIsModifyMode] = useState(false);
    const [expenseToModify, setExpenseToModify] = useState(null);
    const [sortField, setSortField] = useState('modifiedAt');
    const [sortDirection, setSortDirection] = useState('desc');
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [errors, setErrors] = useState({});
    const [userId, setUserId] = useState(null);
    const pageSize = 10;

    const categories = [
        'Food & Dining',
        'Transportation',
        'Utilities',
        'Shopping',
        'Healthcare',
        'Education',
        'Entertainment',
        'Travel',
        'Miscellaneous',
    ];

    const validCurrencies = ['USD', 'EUR', 'GBP', 'INR', 'JPY', 'AUD', 'CAD', 'CNY', 'CHF'];


    // Decode JWT and extract email
    const getEmailFromToken = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login';
            return null;
        }

        try {
            const decoded = jwtDecode(token);
            return decoded.sub || null;
        } catch {
            console.error('Invalid token');
            return null;
        }
    };

    // Fetch `userId` using email
    const fetchUserId = async () => {
        const email = getEmailFromToken();
        if (!email) return;

        try {
            const response = await API.get(`/expenses/findUserIdByEmail`, { params: { email } });
            setUserId(response.data);
        } catch (error) {
            console.error('Error fetching userId:', error);
        }
    };

    useEffect(() => {
        fetchUserId();
    }, []);

    useEffect(() => {
        if (userId) fetchExpenses();
    }, [userId, currentPage, sortField, sortDirection]);

    const fetchExpenses = () => {
        API.get(`/expenses/${userId}`, {
            params: { page: currentPage, size: pageSize, sortBy: sortField, sortDirection },
        })
            .then((response) => {
                setExpenses(response.data.content || []);
                setTotalPages(response.data.totalPages);
            })
            .catch((error) => console.error('Error fetching expenses:', error));
    };

    const handleModalToggle = () => setShowModal(!showModal);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        const error = validateField(name, value);
        setErrors((prev) => ({ ...prev, [name]: error }));
        setNewExpense((prev) => ({ ...prev, [name]: value }));
    };

    const handleSaveExpense = () => {
        if (!newExpense.category || !newExpense.payee || !newExpense.amount || !newExpense.currency) {
            alert('Please fill in all required fields.');
            return;
        }
    
        const saveRequest = isModifyMode
            ? API.put(`/expenses/${expenseToModify.id}`, newExpense)
            : API.post('/expenses', { ...newExpense, userId });
    
        saveRequest
            .then((response) => {
                if (isModifyMode) {
                    // Update modified expense
                    setExpenses((prev) =>
                        prev.map((exp) => (exp.id === expenseToModify.id ? response.data : exp))
                    );
                } else {
                    if (currentPage === totalPages - 1 && expenses.length < pageSize) {
                        // If last page and has space, just add the new expense
                        setExpenses((prev) => [...prev, response.data]);
                    } else if (currentPage === totalPages - 1 && expenses.length === pageSize) {
                        // If last page is full, go to the next page
                        setCurrentPage(totalPages);
                    } else {
                        // If not on the last page, refetch expenses to reset pagination properly
                        fetchExpenses();
                    }
                }
                resetForm();
            })
            .catch((error) => console.error('Error saving expense:', error));
    };
    

    const resetForm = () => {
        setNewExpense({ category: '', payee: '', amount: '', currency: '', description: '' });
        setShowModal(false);
        setIsModifyMode(false);
        setExpenseToModify(null);
    };

    const handleSelectAll = (e) => {
        setSelectedExpenses(e.target.checked ? expenses.map((exp) => exp.id) : []);
    };

    const handleSelectOne = (id) => {
        setSelectedExpenses((prev) =>
            prev.includes(id) ? prev.filter((expId) => expId !== id) : [...prev, id]
        );
    };

    const handleDeleteExpenses = () => {
        if (selectedExpenses.length === 0) {
            alert('Please select at least one record to delete.');
            return;
        }
        setShowConfirm(true);
    };

    const confirmDeleteExpenses = () => {
        API.delete('/expenses', { data: selectedExpenses })
            .then(() => {
                setExpenses((prev) => prev.filter((exp) => !selectedExpenses.includes(exp.id)));
                setSelectedExpenses([]);
                if(expenses.length===selectedExpenses.length && currentPage>0){
                    setCurrentPage((prev)=>prev-1);
                }
                else{
                    fetchExpenses();
                }
                setShowConfirm(false);
            })
            .catch((error) => console.error('Error deleting expenses:', error));
    };

    const handleModifyExpense = (id) => {
        const expense = expenses.find((exp) => exp.id === id);
        setExpenseToModify(expense);
        setNewExpense(expense);
        setIsModifyMode(true);
        setShowModal(true);
    };

    const toggleSortField = (field) => {
        if (sortField === field) {
            setSortDirection((prevDirection) => (prevDirection === 'asc' ? 'desc' : 'asc'));
        } else {
            setSortField(field);
            setSortDirection('asc');
        }
    };

    const validateField = (name, value) => {
        switch (name) {
            case 'category':
                return value ? '' : 'Category is required.';
            case 'payee':
                return value ? '' : 'Payee is required.';
            case 'amount':
                return value > 0 ? '' : 'Amount must be greater than 0.';
            default:
                return '';
        }
    };

    return (
        <div
    style={{
        background: 'linear-gradient(to bottom, #c4dbdb, #88bdce, #c4dbdb)',
        minHeight: '100vh',
        margin: 0,
        padding: 0,
        display: 'flex',
        flexDirection: 'column',
    }}>
        <div className="container mt-4">
            {/* Header */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Expenses</h2>
                <div className="d-flex gap-3">
                    <button className="btn btn-link" title="Add Expense" onClick={handleModalToggle}>
                        <FaPlus />
                    </button>
                    <Dropdown>
                        <Dropdown.Toggle as="button" className="btn btn-link" title="Sort">
                            <FaSort />
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                            <Dropdown.Item onClick={() => toggleSortField('modifiedAt')}>
                                Sort by Modified Date
                            </Dropdown.Item>
                            <Dropdown.Item onClick={() => toggleSortField('createdAt')}>
                                Sort by Date Added
                            </Dropdown.Item>
                            <Dropdown.Item onClick={()=>toggleSortField('amount')}>
                                Sort by Amount
                            </Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                    <button
                        className="btn btn-link"
                        title="Delete Selected"
                        onClick={handleDeleteExpenses}
                        style={{ color: 'red' }}
                    >
                        <FaTrash />
                    </button>
                </div>
            </div>

            {/* Table */}
            <Table>
                <thead>
                    <tr>
                        <th>
                            <Form.Check type="checkbox" onChange={handleSelectAll} />
                        </th>
                        <th>Category</th>
                        <th>Payee</th>
                        <th>Amount</th>
                        <th>Currency</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {expenses.map((expense) => (
                        <tr key={expense.id}>
                            <td>
                                <Form.Check
                                type="checkbox"
                                checked={selectedExpenses.includes(expense.id)}
                                onChange={() => handleSelectOne(expense.id)}
                                />
                            </td>
                            <td>{expense.category}</td>
                            <td>{expense.payee}</td>
                            <td>{expense.amount}</td>
                            <td>{expense.currency}</td>
                            <td>{expense.description}</td>
                            <td>
                                <button
                                className="btn btn-warning btn-sm"
                                onClick={() => handleModifyExpense(expense.id)}
                                >
                                <FaEdit />
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>


            {/* Pagination */}
            <div className="d-flex justify-content-between mt-3">
                <Button
                    variant="secondary"
                    disabled={currentPage === 0}
                    onClick={() => setCurrentPage((prev) => prev - 1)}
                >
                    Previous
                </Button>
             
                <span>
                    Page {expenses.length === 0 ? 0 : currentPage + 1} of {totalPages === 0 ? 1 : totalPages}
                </span>
              
                <Button
                    variant="secondary"
                    disabled={currentPage === totalPages - 1}
                    onClick={() => setCurrentPage((prev) => prev + 1)}
                >
                    Next
                </Button>
            </div>

            {/* Add/Edit Modal */}
            <Modal show={showModal} onHide={resetForm}>
                <Modal.Header closeButton>
                    <Modal.Title>{isModifyMode ? 'Modify Expense' : 'Add Expense'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group>
                            <Form.Label>Category</Form.Label>
                            <Form.Select
                                name="category"
                                value={newExpense.category}
                                onChange={handleInputChange}
                            >
                                <option value="">Select a category</option>
                                {categories.map((category) => (
                                    <option key={category} value={category}>
                                        {category}
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Payee</Form.Label>
                            <Form.Control
                                type="text"
                                name="payee"
                                value={newExpense.payee}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Amount</Form.Label>
                            <Form.Control
                                type="number"
                                name="amount"
                                value={newExpense.amount}
                                onChange={handleInputChange}
                                isInvalid={!!errors.amount}
                            />
                            <Form.Control.Feedback type="invalid">{errors.amount}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Currency</Form.Label>
                            <Form.Select
                                name="currency"
                                value={newExpense.currency}
                                onChange={handleInputChange}
                            >
                                <option value="">Select a currency</option>
                                {validCurrencies.map((currency) => (
                                    <option key={currency} value={currency}>
                                        {currency}
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>

                        <Form.Group>
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="description"
                                value={newExpense.description}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetForm}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleSaveExpense}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Confirm Delete Modal */}
            <Modal show={showConfirm} onHide={() => setShowConfirm(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirm Deletion</Modal.Title>
                </Modal.Header>
                <Modal.Body>Are you sure you want to delete the selected expenses?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirm(false)}>
                        No
                    </Button>
                    <Button variant="danger" onClick={confirmDeleteExpenses}>
                        Yes
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
        </div>
    );
};

export default Expenses;
