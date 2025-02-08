import React, { useState, useEffect } from "react";
import API from "../api";
import { Modal, Button } from "react-bootstrap";
import Papa from "papaparse";
import CategorySpendingCharts from "./CategorySpendingCharts";

const CategoryWiseSpending = ({ selectedCurrency, showModal, closeModal }) => {
  const [expenses, setExpenses] = useState([]);

  useEffect(() => {
    if (selectedCurrency) {
      API.get(`/expenses/category-spending`, {
        params: { currency: selectedCurrency },
      })
        .then((response) => {
          setExpenses(response.data || []);
        })
        .catch((error) => console.error("Error fetching category spending:", error));
    }
  }, [selectedCurrency]);

  const downloadCSV = () => {

    if (!expenses || expenses.length === 0) {
      console.error("Data is empty or undefined. Cannot export.");
      return;
    }

    try {
      const formattedExpenses = expenses.map((expense) => ({
        "Category": expense.category,
        "Total Amount": expense.totalAmount.toFixed(2),
        "Percentage Share": expense.percentageShare.toFixed(2)+"%",
      }));

      console.log("Formatted Expenses for CSV export:", formattedExpenses);

      // Create CSV string using PapaParse
      const csv = Papa.unparse(formattedExpenses);
      const link = document.createElement("a");
      link.href = "data:text/csv;charset=utf-8," + encodeURI(csv);
      link.target = "_blank";
      link.download = "Category Wise Spending.csv"; // Set the CSV file name
      document.body.appendChild(link);
      link.click(); // Trigger the download
      document.body.removeChild(link);

      console.log("CSV export successful");
    } catch (error) {
      console.error("Error exporting CSV:", error);
    }
  };

  return (
    <Modal show={showModal} onHide={closeModal} dialogClassName="modal-dynamic" size="xl">
      <Modal.Header closeButton>
        <Modal.Title>Category Wise Spending</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div>
          <h5 className="font-style" style={{display:'flex', justifyContent: "center"}}>Shows total spending and percentage share of each category</h5>
          <div className="modal-body">
          {
            /*
            {expenses.length > 0 ? (
            <ul>
              {expenses.map((expense, index) => (
                <li key={index}>
                  {expense.category}: {expense.totalAmount} ({expense.percentageShare}%)
                </li>
              ))}
            </ul>
          ) : (
            <p>No data available for the selected currency.</p>
          )}
            */
          }
          <CategorySpendingCharts expenses={expenses}>
            
          </CategorySpendingCharts>
          <div style={{
              marginTop: "20px",
              padding: "10px",
              backgroundColor: "#f8f9fa",
              borderRadius: "5px",
              fontSize: "14px",
              color: "#555",
              textAlign: "justify"
            }}>
              <strong>Disclaimer:</strong> 
              <p>
                This visualization helps understand <strong>category-wise spending distribution</strong>.
                The <strong>pie chart</strong> shows <strong>proportions</strong>, while the <strong>bar chart</strong> displays absolute amounts.
                If a category does not appear, it means <strong>no transactions were recorded</strong> in that category.
              </p>
            </div>
          </div>
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="success" onClick={downloadCSV} style={{ marginLeft: "20px" }} title="Download CSV File">
          Export
        </Button>
        <Button variant="danger" onClick={closeModal}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default CategoryWiseSpending;
