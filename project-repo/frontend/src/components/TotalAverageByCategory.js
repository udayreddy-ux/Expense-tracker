import React, { useState, useEffect } from "react";
import { Dropdown, Modal, Button } from "react-bootstrap";
import API from "../api";
import Papa from "papaparse";
import TotalAverageCharts from "./TotalAverageCharts";

const TotalAverageByCategory = ({ selectedCurrency, showModal, closeModal }) => {
  console.log("This is totalAverage");

  const [expenses, setExpenses] = useState([]);
  const [selectedMonth, setMonth] = useState(null);
  const [selectedYear, setYear] = useState(null);
  const [years, setAvailableYears] = useState([]);
  const [loading, setLoading] = useState(false); // 🚀 Handle Loading State
  const [noData, setNoData] = useState(false);  // 🚀 Track if No Data is Available

  const months = [
    "January", "February", "March", "April", "May", "June", 
    "July", "August", "September", "October", "November", "December"
  ];

  /*** ✅ Fetch Available Years on Mount ***/
  useEffect(() => {
    if (selectedCurrency) {
      API.get(`/expenses/years`)
        .then((response) => {
          setAvailableYears(response.data || []);
        })
        .catch((error) => {
          console.error("No data available for years:", error);
        });
    }
  }, [selectedCurrency]);

  /*** ✅ Fetch Data when Month or Year Changes ***/
  useEffect(() => {
    if (selectedCurrency && selectedYear && selectedMonth) {
      setLoading(true);
      setNoData(false);  // Reset No Data Status
      
      API.get(`/expenses/averagesharebycategory`, {
        params: { monthName: selectedMonth, year: selectedYear, currency: selectedCurrency },
      })
        .then((response) => {
          const data = response.data || [];
          setExpenses(data);
          setNoData(data.length === 0); // 🚀 If no data, set the flag
        })
        .catch((error) => {
          console.error("Error fetching monthly spend data:", error);
          setNoData(true);
        })
        .finally(() => {
          setLoading(false);
        });
    }
  }, [selectedCurrency, selectedYear, selectedMonth]);

  /*** ✅ CSV Export Function ***/
  const downloadCSV = () => {
    if (!expenses || expenses.length === 0) {
      console.error("Data is empty or undefined. Cannot export.");
      return;
    }

    try {
      const formattedExpenses = expenses.map((expense) => ({
        "Category": expense.category,
        "Total Amount": expense.totalAmount.toFixed(2),
        "Percentage Share": expense.percentageShare.toFixed(2) + "%",
      }));

      const csv = Papa.unparse(formattedExpenses);
      const link = document.createElement("a");
      link.href = "data:text/csv;charset=utf-8," + encodeURI(csv);
      link.target = "_blank";
      link.download = "TotalAverageByCategory.csv";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      console.log("CSV export successful");
    } catch (error) {
      console.error("Error exporting CSV:", error);
    }
  };

  return (
    <div>
      <Modal show={showModal} onHide={closeModal} dialogClassName="modal-dynamic" size="xl">
        <Modal.Header closeButton>
          <Modal.Title>Month wise analysis</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div style={{ display: "flex", flexDirection: "column" }}>
            <h5 className="font-style">Analyzes total spending per month for the year {selectedYear}</h5>
            
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: "20px" }}>
              {/* ✅ Month Dropdown */}
              <Dropdown onSelect={(month) => setMonth(month)}>
                <Dropdown.Toggle variant="info" id="dropdown-basic">
                  {selectedMonth ? selectedMonth : "Select Month"}
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  {months.map((month, index) => (
                    <Dropdown.Item key={index} eventKey={month}>
                      {month}
                    </Dropdown.Item>
                  ))}
                </Dropdown.Menu>
              </Dropdown>

              {/* ✅ Year Dropdown */}
              <Dropdown onSelect={(year) => setYear(year)} style={{ marginLeft: "20px" }}>
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

            {/* ✅ Show Loading Indicator */}
            {loading && <p>Loading data...</p>}

            {/* ✅ Show "No Data Available" Message */}
            {noData && !loading && (
              <p style={{ textAlign: "center", fontSize: "18px", color: "gray" }}>No Data Available for the selected month and year.</p>
            )}

            {/* ✅ Show Charts Only If Data Exists */}
            {!noData && !loading && <TotalAverageCharts expenses={expenses} />}
            <p style={{ fontSize: "14px", marginTop: "15px", color: "#555" }}>
              <strong>Disclaimer:</strong><br/> 
              These visualizations analyze total spending trends by category and month. 
              The donut chart shows the proportional distribution, while the bar chart represents absolute amounts. 
              If no data is available for the selected filters, the charts will remain empty. 
              When only one category exists, the bar may not appear due to scaling. 
              These insights help track spending but should be interpreted alongside other financial factors.
            </p>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="success" onClick={downloadCSV} style={{ marginLeft: "20px" }} title="Download CSV File">
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

export default TotalAverageByCategory;
