import React, { useEffect, useState } from "react";
import API from "../api";
import { Container, Card, Button } from "react-bootstrap";
import { BsCashStack } from "react-icons/bs";
import { MdAddCircle } from "react-icons/md";
import { useNavigate } from "react-router-dom";
import CategorySpending from "../components/CategorySpending";

const Home = () => {
    const [user, setUser] = useState({ first_name: "" });
    const [transactions, setTransactions] = useState([]);
    const [userDetails, getUserDetails] = useState([]);
    const [payee, setPayee] = useState([]);
    const currency = "USD";
    const navigate = useNavigate();

    useEffect(() => {
        fetchUserDetails();
        fetchTransactions();
        fetchAccountDetails();
        fetchTopPayee();
    }, []);

    const fetchUserDetails = () => {
        API.get(`/users/Profile`)
            .then((response) => setUser({ first_name: response.data.first_name || "" }))
            .catch((error) => console.error("Error fetching details"));
    };

    const fetchTopPayee = () => {
        API.get(`/expenses/getTopPayees`, { params: { currency } })
            .then((response) => setPayee(response.data.length > 0 ? response.data[0] : null))
            .catch((error) => console.error("Error fetching payee:", error));
    };

    const fetchTransactions = () => {
        API.get(`/expenses/getRecentTransactions`, { params: { currency } })
            .then((response) => setTransactions(response.data.length > 0 ? response.data.slice(0, 9) : []))
            .catch((error) => console.error("Error fetching transactions:", error));
    };

    const fetchAccountDetails = () => {
        API.get(`/expenses/getTotalAmount`, { params: { currency } })
            .then((response) => getUserDetails(response.data.length > 0 ? response.data : []))
            .catch((error) => console.error("Error fetching data"));
    };

    return (
        <div
            style={{
                background: "linear-gradient(to bottom, #c4dbdb, #88bdce, #c4dbdb)",
                minHeight: "100vh",
                height: "auto",
                width: "100%",
                margin: 0,
                paddingBottom: "50px",
                display: "flex",
                flexDirection: "column",
            }}
        >
            <Container fluid style={{ display: "flex", flexDirection: "row", padding: 0 }}>
                {/* Left Sidebar - Recent Transactions */}
                <Container style={{ width: "30%", margin: "0px 10px", display: "flex", flexDirection: "column" }}>
                    <div className="align-home-heading">
                        <span className="emoji-spin" style={{ fontSize: "20px" }}>👋</span>
                        <span className="analytics-dashboard-heading" style={{ fontSize: "23px", textAlign: "left" }}>
                            Welcome, {user.first_name}!
                        </span>
                    </div>
                    <Container style={{ display: "flex", justifyContent: "center", flexGrow: 1 }}>
                        <Card style={{ flexGrow: 1, height: "100%", border: "none", background: "transparent", boxShadow: "none", padding: "15px", overflowY: "auto" }}>
                            <h5 style={{ textAlign: "center", fontSize: "20px", fontWeight: "600", marginTop: "10px" }}>Recent Transactions</h5>
                            <hr style={{ borderTop: "2px solid #ccc", margin: "10px 0" }} />

                            {transactions.length > 0 ? (
                                transactions.map((txn, index) => (
                                    <div key={index} style={{ display: "flex", alignItems: "center", padding: "12px 8px", gap: "12px" }}>
                                        <div style={{ flexGrow: 1, textAlign: "left" }}>
                                            <h6 style={{ margin: 0, fontWeight: "bold", whiteSpace: "nowrap" }}>{txn.payee}</h6>
                                            <p style={{ margin: 0, fontSize: "12px", color: "#777" }}>{txn.createdAt}</p>
                                        </div>
                                        <BsCashStack style={{ fontSize: "22px", color: "#007bff", minWidth: "30px" }} />
                                        <h6 style={{ margin: 0, fontWeight: "bold", color: "#28a745", minWidth: "70px", textAlign: "right" }}>
                                            ${txn.amount}
                                        </h6>
                                    </div>
                                ))
                            ) : (
                                <div style={{ textAlign: "center", padding: "20px", color: "#888" }}>
                                    <span style={{ fontSize: "30px" }}>😔</span>
                                    <p>No transactions available</p>
                                </div>
                            )}

                            <div style={{ textAlign: "center", marginTop: "15px" }}>
                                <Button onClick={() => navigate("/Dashboard")} variant="primary">
                                    <MdAddCircle style={{ fontSize: "20px", marginRight: "5px" }} />
                                    More info
                                </Button>
                            </div>
                        </Card>
                    </Container>
                </Container>

                {/* Right Section - Analytics */}
                <Container style={{ flexGrow: 1, margin: "20px 10px", display: "flex", flexDirection: "column" }}>
                    <Container style={{ display: "flex", justifyContent: "space-between", gap: "15px" }}>
                        <Card style={{ flex: 1, padding: "10px", textAlign: "center", border: "none", background: "transparent", boxShadow: "none", minHeight: "100px" }}>
                            <h4 style={{ color: "blue" }}>{userDetails.length > 0 ? `${userDetails[0].numberOfTransactions}` : "Loading..."}</h4>
                            <p>Number of Transactions</p>
                        </Card>
                        <Card style={{ flex: 1, padding: "10px", textAlign: "center", border: "none", background: "transparent", boxShadow: "none", minHeight: "100px" }}>
                            <h4 style={{ color: "blue" }}>{userDetails.length > 0 ? `$${userDetails[0].totalAmount}` : "Loading..."}</h4>
                            <p>Total Spendings</p>
                        </Card>
                        <Card style={{ flex: 1, padding: "10px", textAlign: "center", border: "none", background: "transparent", boxShadow: "none", minHeight: "100px" }}>
                            <h4 style={{ color: "blue" }}>{userDetails.length > 0 ? `${userDetails[0].totalPayees}` : "Loading..."}</h4>
                            <p>Number of Payees</p>
                        </Card>
                        <Card style={{ flex: 1, padding: "10px", textAlign: "center", border: "none", background: "transparent", boxShadow: "none", minHeight: "100px" }}>
                            <h4 style={{ color: "blue" }}>{payee ? `${payee.payee}` : "Loading..."}</h4>
                            <p>Top Recipient</p>
                        </Card>
                    </Container>

                    {/* Category Spending Charts */}
                    <Container style={{ flexGrow: 1, display: "flex", marginTop: "15px", background: "transparent", border: "none", boxShadow: "none" }}>
                        <CategorySpending />
                        
                    </Container>
                    <p style={{ fontSize: "14px", marginTop: "15px", color: "#555" }}>
              <strong>Disclaimer:</strong><br/> 
              If this section says 'Loading...', please wait a moment. If you are a new user or haven't added any transactions yet, you may not see any data here. Click on "more info" to add expenses!!
            </p>
                </Container>
            </Container>
        </div>
    );
};

export default Home;
