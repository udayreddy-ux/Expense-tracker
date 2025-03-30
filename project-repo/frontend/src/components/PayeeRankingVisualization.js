import React from "react";
import { Modal,Button } from "react-bootstrap";
import API from "../api";
import { useState,useEffect } from "react";
import Papa from "papaparse";
import PayeeRankCharts from "./PayeeRankCharts";
const PayeeRankingVisualization=({selectedCurrency,showModal,closeModal})=>{
    const [expenses,setExpenses]=useState([]);
    useEffect(() => {
        if (selectedCurrency) {
          API.get(`/expenses/payeeranks`, {
            params: { currency: selectedCurrency },
          })
            .then((response) => {
              setExpenses(response.data || []);
            })
            .catch((error) => console.error("Error fetching category spending:", error));
        }
      }, [selectedCurrency]);

      const downloadCSV = () =>{
        if(!expenses || expenses.length===0){
          console.log("Data is empty or undefined. Cannot export.");
          return;
        }
        try{
          const formattedExpenses= expenses.map((expense)=>({
            "Payee" : expense.payee,
            "Total Amount" : expense.totalAmount,
            "Percentage Share" : expense.percentageShare+"%",
            "Rank" : expense.payeerank,
          }));
          const csv=Papa.unparse(formattedExpenses);
          const link=document.createElement("a");
          link.href="data:text/csv;charset=utf-8," + encodeURI(csv);
          link.target="_blank";
          link.download="Payee Rankings.csv"
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
        }catch (error) {
          console.error("Error exporting CSV:", error);
        }
      }
  return (
    <Modal show={showModal} onHide={closeModal} dialogClassName="modal-dynamic" size="xl">
      <Modal.Header closeButton>
        <Modal.Title>Top Payees and rankings</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div>
          <h5 className="font-style">Analyzes spending patterns for each payee</h5>
          <PayeeRankCharts expenses={expenses}>

          </PayeeRankCharts>
          <p style={{fontSize: "14px", marginTop: "15px", color: "#555"}}>
            <strong>Disclaimer</strong><br/>
            The horizontal bar chart represents the ranking of payees based on the total amount spent. 
            Each bar corresponds to a payee, with the length indicating the total expenditure.
            If no data is available for the selected month or year, the chart will remain empty. 
            Additionally, when only one payee exists, the axis may still be displayed, but no bar will be visible.
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
  );
}
export default PayeeRankingVisualization;