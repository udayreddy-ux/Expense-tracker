import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const TotalAverageCharts = ({ expenses }) => {
  const donutChart = useRef(null);
  const barChart = useRef(null);
  const tooltipRef = useRef(null);
  const containerRef = useRef(null);
  const legendRef = useRef(null);

  useEffect(() => {
    if (!expenses || expenses.length === 0) return;

    const data = expenses.map((expense) => ({
      category: expense.category,
      value: expense.totalAmount,
      share: expense.percentageShare,
    }));
    const categories = Array.from(new Set(expenses.map((d) => d.category)));

    /*** ✅ Donut Chart Adjustments ***/
    const donWidth = 350;
    const donHeight = 350;
    const outerRadius = Math.min(donWidth, donHeight) / (categories.length === 1 ? 3 : 2);
    const innerRadius = outerRadius * 0.6;

    d3.select(donutChart.current).selectAll("*").remove();

    const donsvg = d3
      .select(donutChart.current)
      .attr("width", donWidth)
      .attr("height", donHeight)
      .append("g")
      .attr("transform", `translate(${donWidth / 2},${donHeight / 2})`);

    const colorScale = d3.scaleOrdinal(d3.schemeCategory10);
    const arc = d3.arc().innerRadius(innerRadius).outerRadius(outerRadius);
    const pieGenerator = d3.pie().value((expense) => expense.share).sort(null);
    const arcHover = d3.arc().innerRadius(innerRadius).outerRadius(outerRadius + 10);
    const donData = pieGenerator(data);

    /*** ✅ Tooltip Setup ***/
    const tooltip = d3
      .select(tooltipRef.current)
      .style("position", "absolute")
      .style("background", "#fff")
      .style("border", "1px solid #ccc")
      .style("padding", "8px")
      .style("border-radius", "5px")
      .style("box-shadow", "0px 4px 6px rgba(0,0,0,0.2)")
      .style("display", "none")
      .style("pointer-events", "none")
      .style("z-index", "9999");

    /*** ✅ Draw Donut Chart ***/
    donsvg
      .selectAll(".arc")
      .data(donData)
      .enter()
      .append("g")
      .attr("class", "arc")
      .append("path")
      .attr("d", arc)
      .style("fill", (expense) => colorScale(expense.data.category))
      .on("mouseover", function (event, expense) {
        d3.select(this).transition().duration(200).attr("d", arcHover);

        const { category, value, share } = expense.data;
        const rect = event.target.getBoundingClientRect();
        const modalRect = containerRef.current.getBoundingClientRect();

        tooltip
          .style("display", "block")
          .html(
            `<strong>Category:</strong> ${category}<br/>
             <strong>Amount:</strong> ${value}<br/>
             <strong>Percentage:</strong> ${share.toFixed(2)}%`
          )
          .style("left", `${rect.left + window.scrollX - modalRect.left + 10}px`)
          .style("top", `${rect.top + window.scrollY - modalRect.top - 30}px`);
      })
      .on("mouseout", function () {
        d3.select(this).transition().duration(200).attr("d", arc);
        tooltip.style("display", "none");
      });

    /*** ✅ Legend Adjustment - Positioned Right Beside Donut Chart ***/
    const legend = d3.select(legendRef.current);
    legend.selectAll("*").remove(); // Clear previous legends
    legend
      .selectAll(".legend-item")
      .data(data)
      .enter()
      .append("div")
      .attr("class", "legend-item")
      .style("display", "flex")
      .style("align-items", "center")
      .style("margin-bottom", "5px")
      .html(
        (expense) =>
          `<div style="width: 15px; height: 15px; background: ${colorScale(
            expense.category
          )}; margin-right: 10px;"></div>${expense.category}`
      );

    /*** ✅ Bar Chart Adjustments ***/
    const modal = document.querySelector(".modal-dialog");
    const modalWidth = modal ? modal.clientWidth : 900;

    const barWidth = Math.min(modalWidth - 200, Math.max(categories.length * 90, 200));
    const barHeight = 400;
    const margin = { top: 20, right: 30, bottom: 70, left: 60 };

    d3.select(barChart.current).selectAll("*").remove();

    const barSvg = d3
      .select(barChart.current)
      .attr("width", barWidth)
      .attr("height", barHeight)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    const xScale = d3
      .scaleBand()
      .domain(categories)
      .range([0, Math.max(100, barWidth - margin.left - margin.right)])
      .padding(categories.length === 1 ? 0.5 : 0.3); // Ensure width for single category

    const yScale = d3
      .scaleLinear()
      .domain([0, d3.max(data, (expense) => expense.value) || 1])
      .nice()
      .range([barHeight - margin.top - margin.bottom, 0]);

    barSvg
      .append("g")
      .attr("transform", `translate(0, ${barHeight - margin.top - margin.bottom})`)
      .call(d3.axisBottom(xScale))
      .selectAll("text")
      .attr("transform", "rotate(-20)")
      .style("text-anchor", "end");

    barSvg.append("g").call(d3.axisLeft(yScale));

    /*** ✅ Draw Bars with Hover Effects ***/
    barSvg
      .selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", (expense) => xScale(expense.category))
      .attr("y", (expense) => yScale(expense.value))
      .attr("width", xScale.bandwidth())
      .attr("height", (expense) => barHeight - margin.top - margin.bottom - yScale(expense.value))
      .attr("fill", (expense) => colorScale(expense.category))
      .on("mouseover", function (event, expense) {
        d3.select(this).attr("stroke", "black").attr("stroke-width", 2);

        const rect = event.target.getBoundingClientRect();
        const modalRect = containerRef.current.getBoundingClientRect();
        const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10;
        const topPos = rect.top - modalRect.top + modal.scrollTop - 10;

        tooltip
          .style("display", "block")
          .html(
            `<strong>Category:</strong> ${expense.category}<br/>
             <strong>Amount:</strong> ${expense.value}`
          )
          .style("left", `${leftPos}px`)
          .style("top", `${topPos}px`);
      })
      .on("mouseout", function () {
        d3.select(this).attr("stroke", "none");
        tooltip.style("display", "none");
      });
  }, [expenses]);

  return (
    <div className="visualization-container" style={{ display: "flex", justifyContent: "center", gap: "40px" }}>
      <div ref={containerRef} className="pie-and-legend-container" style={{ display: "flex", alignItems: "center", gap: "30px" }}>
        <div>
          <h5>Donut Chart</h5>
          <svg ref={donutChart}></svg>
        </div>
        <div ref={legendRef} className="legend-container"></div>
      </div>
      <div className="chart">
        <h5>Bar Chart</h5>
        <svg ref={barChart}></svg>
      </div>
      <div ref={tooltipRef} className="d3-tooltip"></div>
    </div>
  );
};

export default TotalAverageCharts;
