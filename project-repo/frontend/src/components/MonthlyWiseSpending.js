import React, { useEffect, useState } from "react";
import { Dropdown, Modal, Button } from "react-bootstrap";
import API from "../api";
import Papa from "papaparse";
import MonthlySpendingCharts from "./MonthlySpendingCharts";

const MonthlyWiseSpending = ({ selectedCurrency, showModal, closeModal }) => {
  const [expenses, setExpenses] = useState([]);
  const [selectedYear, setYear] = useState();
  const [years, setAvailableYears] = useState([]);
  const [currencies, setCurrency] = useState([]);
  useEffect(() => {
    if (selectedCurrency) {
      API.get(`/expenses/years`)
        .then((response) => setAvailableYears(response.data || []))
        .catch((error) => console.error("No data available for years:", error));

      if (selectedYear) {
        API.get(`/expenses/monthly-spend`, {
          params: { year: selectedYear, currency: selectedCurrency },
        })
          .then((response) => setExpenses(response.data || []))
          .catch((error) => console.error("Error fetching monthly spend data:", error));

        API.get(`/expenses/monthly-currency-spend`,{
          params: {year: selectedYear},
        })
          .then((response) => setCurrency(response.data || []))
          .catch((error)=> console.error("Error fetching currency data:", error));
      }
    }
  }, [selectedCurrency, selectedYear]);

  console.log(currencies);

  const downloadCSV = () => {
    if (!expenses || expenses.length === 0) {
      console.log("Data not available");
      return;
    }
    try {
      const formattedExpenses = expenses.map((expense) => ({
        "Month": expense.monthName,
        "Year": expense.year,
        "Total Amount": expense.totalAmount.toFixed(2),
      }));
      const csv = Papa.unparse(formattedExpenses);
      const link = document.createElement("a");
      link.href = "data:text/csv;charset=utf-8," + encodeURI(csv);
      link.target = "_blank";
      link.download = "Month_Wise_Spending.csv";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error("Error exporting CSV:", error);
    }
  };

  return (
    <div>
      <Modal show={showModal} onHide={closeModal} size="xl" dialogClassName="modal-dynamic">
        <Modal.Header closeButton>
          <Modal.Title>Month-Wise Analysis</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div style={{ display: "flex", flexDirection: "column" }}>
            <h5 className="font-style">
              This analysis provides insights into total spending per month for the year {selectedYear}.
            </h5>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: "20px" }}>
              <Dropdown onSelect={(year) => setYear(year)}>
                <Dropdown.Toggle variant="info" id="dropdown-basic">
                  {selectedYear ? selectedYear : "Select Year"}
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  {years.map((year, index) => (
                    <Dropdown.Item key={index} eventKey={year}>
                      {year}
                    </Dropdown.Item>
                  ))}
                </Dropdown.Menu>
              </Dropdown>
            </div>
            <MonthlySpendingCharts expenses={expenses} currencies={currencies}/>
            <p style={{ fontSize: "14px", marginTop: "15px", color: "#555" }}>
              <strong>Disclaimer:</strong><br/> 
              This visualization provides insights into your spending patterns.
              If a month does not appear, it means no transactions were recorded during that month.
              The stacked bar chart shows the overall spending for each type of currency, while the line chart visualizes trends over time.
            </p>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="success" onClick={downloadCSV} title="Download CSV File" style={{ marginLeft: "20px" }}>
            Export
          </Button>
          <Button variant="secondary" onClick={closeModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default MonthlyWiseSpending;
