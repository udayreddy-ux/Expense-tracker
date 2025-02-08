import React, { useEffect, useState } from "react";
import API from "../api";
import { Dropdown, Modal, Button } from "react-bootstrap";
import Papa from "papaparse";
import CategoryMonthlyCharts from "./CategoryMonthlyCharts";

const CategoryMonthlyDistribution = ({ selectedCurrency, showModal, closeModal }) => {
  const [expenses, setExpenses] = useState([]);
  const [selectedYear, setYear] = useState();
  const [years, setAvailableYears] = useState([]);

  useEffect(() => {
    if (selectedCurrency) {
      API.get(`/expenses/years`)
        .then((response) => setAvailableYears(response.data || []))
        .catch((error) => console.error("No data available for years: ", error));

      if (selectedYear) {
        API.get(`/expenses/monthwisecategory`, {
          params: { year: selectedYear, currency: selectedCurrency },
        })
          .then((response) => setExpenses(response.data || []))
          .catch((error) => console.error("Error fetching the expenses: ", error));
      }
    }
  }, [selectedCurrency, selectedYear]);

  const downloadCSV= ()=>{
    if(!expenses || expenses.length===0){
      console.log("Data is empty or undefined. Cannot export.");
      return;
    }
    try{
        const formattedExpenses=expenses.map((expense)=>({
            "Month":expense.monthName,
            "Year":expense.year,
            "Category":expense.category,
            "Total Amount": expense.totalAmount.toFixed(2),
        }));
        const csv=Papa.unparse(formattedExpenses);
        const link=document.createElement("a");
        link.href="data:text/csv;charset=utf-8," + encodeURI(csv);
        link.target="_blank";
        link.download="Category Monthly Distribution.csv"
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        console.log("CSV export successful");
    } catch(error){
        console.error("Data not available",error);
    }
}
  return (
    <div>
      <Modal show={showModal} onHide={closeModal} size="xl" dialogClassName="modal-dynamic">
        <Modal.Header closeButton>
          <Modal.Title>Month wise Distribution</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div style={{ display: "flex", flexDirection: "column" }}>
            <h5 className="font-style">
              Shows each category’s contribution to the total spending for the month in the year {selectedYear}
            </h5>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: "20px" }}>
              <Dropdown onSelect={(year) => setYear(year)}>
                <Dropdown.Toggle variant="info" id="dropdown-basic">
                  {selectedYear ? selectedYear : "Select year"}
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
            <CategoryMonthlyCharts expenses={expenses} />
                        {/* ✅ Disclaimer Note */}
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
    The <strong>Stacked Bar Chart</strong> and <strong>Heatmap</strong> serve complementary purposes in analyzing your expenses.  
    The <strong>Stacked Bar Chart</strong> provides an <strong>aggregated</strong> view of spending across categories, helping identify <strong>major spending areas</strong>.  
    The <strong>Heatmap</strong> highlights <strong>spending patterns</strong>, allowing users to easily spot <strong>high-expenditure months</strong>.  
    <br /><br />
    If a month is missing in the visualizations, it means <strong>no transactions were recorded for that period</strong>.  
    This tool is designed to <strong>help users gain better financial insights</strong> by visualizing trends over time.
  </p>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="success" onClick={downloadCSV} style={{marginLeft:"20px"}} title="Download CSV File">
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

export default CategoryMonthlyDistribution;
