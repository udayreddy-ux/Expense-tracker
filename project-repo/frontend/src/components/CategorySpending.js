import React, { useEffect, useState } from "react";
import API from "../api";
import Chart from "react-apexcharts";
import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const CategorySpending = () => {
  const [usdExpense, setUsdExpense] = useState([]);
  const [inrExpense, setInrExpense] = useState([]);
  const [usBar, setUsBar] = useState([]);
  const [inrBar, setInrBar] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    API.get(`/expenses/category-spending`, { params: { currency: "USD" } })
      .then((response) => setUsdExpense(response.data || []))
      .catch((error) => console.error("Error fetching USD category data:", error));

    API.get(`/expenses/category-spending`, { params: { currency: "INR" } })
      .then((response) => setInrExpense(response.data || []))
      .catch((error) => console.error("Error fetching INR category data:", error));

    API.get(`/expenses/getTotalSpent`, { params: { currency: "USD" } })
      .then((response) => setUsBar(response.data || []))
      .catch((error) => console.error("Error fetching USD bar chart data:", error));

    API.get(`/expenses/getTotalSpent`, { params: { currency: "INR" } })
      .then((response) => setInrBar(response.data || []))
      .catch((error) => console.error("Error fetching INR bar chart data:", error));
  }, []);

  const usdCategories = usdExpense.map((item) => item.category);
  const usdAmounts = usdExpense.map((item) => item.totalAmount);
  const inrCategories = inrExpense.map((item) => item.category);
  const inrAmounts = inrExpense.map((item) => item.totalAmount);
  const usBarCategories = usBar.map((item) => item.category);
  const usBarAmounts = usBar.map((item) => item.totalAmount);
  const inrBarCategories = inrBar.map((item) => item.category);
  const inrBarAmounts = inrBar.map((item) => item.totalAmount);

  const donutChartOptions = (title, categories) => ({
    chart: { type: "donut", width: "100%" },
    labels: categories,
    plotOptions: { pie: { startAngle: -90, endAngle: 270 } },
    dataLabels: { enabled: true, style: { fontSize: "14px" } },
    fill: { type: "gradient" },
    legend: { position: "bottom", fontSize: "14px" },
    title: {
      text: title,
      align: "center",
      style: { fontSize: "18px", fontWeight: "bold" },
    },
  });

  const barChartOptions = (title, categories) => ({
    chart: { type: "bar", width: "100%" },
    xaxis: { categories },
    title: {
      text: title,
      align: "center",
      style: { fontSize: "18px", fontWeight: "bold" },
    },
    plotOptions: {
      bar: { horizontal: false, columnWidth: "50%" },
    },
    dataLabels: { enabled: false },
    legend: { position: "bottom" },
  });

  return (
    <div style={{ width: "100%", maxWidth: "100%", padding: "20px" }}>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(2, minmax(400px, 1fr))", // Perfect balance
          gap: "30px", // Reduced gap for proper alignment
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        {/* USD Donut Chart */}
        <div style={{ width: "100%", maxWidth: "550px" }}>
          {usdExpense.length > 0 ? (
            <Chart
              options={donutChartOptions("USD Category Spending", usdCategories)}
              series={usdAmounts}
              type="donut"
              height={330} // Adjusted height for uniformity
            />
          ) : (
            <p>Loading USD Donut Chart...</p>
          )}
        </div>

        {/* USD Bar Chart */}
        <div style={{ width: "100%", maxWidth: "550px" }}>
          {usBar.length > 0 ? (
            <Chart
              options={barChartOptions("USD Spending Breakdown", usBarCategories)}
              series={[{ name: "Total Amount", data: usBarAmounts }]}
              type="bar"
              height={330}
            />
          ) : (
            <p>Loading USD Bar Chart...</p>
          )}
        </div>

        {/* INR Bar Chart */}
        <div style={{ width: "100%", maxWidth: "550px", marginTop: "10px" }}>
          {inrBar.length > 0 ? (
            <Chart
              options={barChartOptions("INR Spending Breakdown", inrBarCategories)}
              series={[{ name: "Total Amount", data: inrBarAmounts }]}
              type="bar"
              height={330}
            />
          ) : (
            <p>Loading INR Bar Chart...</p>
          )}
        </div>

        {/* INR Donut Chart */}
        <div style={{ width: "100%", maxWidth: "550px" }}>
          {inrExpense.length > 0 ? (
            <Chart
              options={donutChartOptions("INR Category Spending", inrCategories)}
              series={inrAmounts}
              type="donut"
              height={330}
            />
          ) : (
            <p>Loading INR Donut Chart...</p>
          )}
        </div>
      </div>

      <div style={{ textAlign: "right", marginTop: "20px", paddingRight: "40px" }}>
        <Button variant="info" onClick={() => navigate("/analytics")}>
          View More
        </Button>
      </div>
    </div>
  );
};

export default CategorySpending;
