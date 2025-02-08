import React, { useState } from "react";
import { Card, Container, Row, Col, Button, Dropdown } from "react-bootstrap";
import { visualizations } from "../Data";
import CategoryWiseSpending from "../components/CategoryWiseSpending";
import CategoryMonthlyDistribution from "../components/CategoryMonthlyDistribution";
import MonthlyWiseSpending from "../components/MonthlyWiseSpending";
import PayeeRankingVisualization from "../components/PayeeRankingVisualization";
import TotalAverageByCategory from "../components/TotalAverageByCategory";
const Analytics = () => {
  const [handleCurrency, setCurrencyType] = useState(null); // Selected currency
  const [selectedVisualization, setSelectedVisualization] = useState(null); // Selected visualization
  const [showModal, setModal] = useState(false); // Modal visibility
  const currencies = ["USD", "EUR", "GBP", "INR", "JPY", "AUD", "CAD", "CNY", "CHF"]; // Currency options

  // Open Modal with Selected Visualization
  const openModal = (visualization) => {
    setSelectedVisualization(visualization);
    setModal(true);
  };

  // Close Modal
  const closeModal = () => {
    setModal(false);
    setSelectedVisualization(null);
  };

  // Render the selected visualization in the modal
  const renderVisualization = () => {
    console.log(selectedVisualization)
    switch (selectedVisualization) {
      case "categorySpending":
        return (
          <CategoryWiseSpending
            selectedCurrency={handleCurrency}
            showModal={showModal}
            closeModal={closeModal}
          />
        );
      case "monthlySpending":
        return (
          <MonthlyWiseSpending
            selectedCurrency={handleCurrency}
            showModal={showModal}
            closeModal={closeModal}
          />
        );
      case "CategoryMonthlyDistribution":
        return (
          <CategoryMonthlyDistribution
            selectedCurrency={handleCurrency}
            showModal={showModal}
            closeModal={closeModal}
          />
        );
      
      case "totalAverageByCategory":
        return (
          <TotalAverageByCategory
            selectedCurrency={handleCurrency}
            showModal={showModal}
            closeModal={closeModal}
          />
        );

      case "payeeRanking":
        return (
          <PayeeRankingVisualization
            selectedCurrency={handleCurrency}
            showModal={showModal}
            closeModal={closeModal}
          />
        );
      default:
        return null;
    }
  };

  return (
    <div
      style={{
        background: "linear-gradient(to bottom,#c4dbdb,#88bdce,#c4dbdb)",
        minHeight: "100vh",
        margin: 0,
        padding: 0,
        display: "flex",
        flexDirection: "column",
      }}
    >
      <div
        className={`header-container ${handleCurrency ? "header-flex" : ""}`}
        style={{
          display: "flex",
          justifyContent: handleCurrency ? "space-between" : "center",
          width: "90%",
          margin: handleCurrency ? "20px auto" : "50px auto",
        }}
      >
        <h1 style={{ fontSize: "45px" }} className="analytics-dashboard-heading">
          Analytics Dashboard
        </h1>
        {handleCurrency && (
          <Dropdown
            onSelect={(currency) => setCurrencyType(currency)}
            style={{ alignSelf: "center", marginRight: "20px" }}
          >
            <Dropdown.Toggle variant="info" id="dropdown-basic">
              {handleCurrency}
            </Dropdown.Toggle>
            <Dropdown.Menu>
              {currencies.map((currency, index) => (
                <Dropdown.Item key={index} eventKey={currency}>
                  {currency}
                </Dropdown.Item>
              ))}
            </Dropdown.Menu>
          </Dropdown>
        )}
      </div>

      {!handleCurrency ? (
        <>
          <h5 className="analytics-heading3">Explore Your Spending Insights</h5>
          <h5 className="analytics-heading3">
            Dive into your expenses with detailed visualizations.
          </h5>
          <Card
            className="card-alignment"
            style={{ width: "24rem", margin: "20px auto" }}
          >
            <Card.Body>
              <Card.Title>Currency</Card.Title>
              <Card.Subtitle className="mb-3">
                Select appropriate currency type
              </Card.Subtitle>
              <Container>
                <Row>
                  {currencies.map((currency, index) => (
                    <Col
                      xs={4}
                      key={index}
                      className="d-flex justify-content-center mb-3"
                    >
                      <Button
                        variant="info"
                        onClick={() => setCurrencyType(currency)}
                        style={{ width: "80px" }}
                      >
                        {currency}
                      </Button>
                    </Col>
                  ))}
                </Row>
              </Container>
            </Card.Body>
          </Card>
        </>
      ) : (
        <Container className="visualization-options-container">
          <Row>
            {visualizations.map((option, index) => (
              <Col key={index} sm={12} md={6} lg={4} className="mb-4">
                <Card className="visualization-card">
                  <Card.Body>
                    <div className="visualization-icon">{option.icon}</div>
                    <Card.Title>{option.title}</Card.Title>
                    <Card.Text>{option.description}</Card.Text>
                    <Button
                      variant="primary"
                      onClick={() => {
                        console.log(option)
                        openModal(option.val)}}
                    >
                      Explore
                    </Button>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </Container>
      )}
      {renderVisualization()}
    </div>
  );
};

export default Analytics;
