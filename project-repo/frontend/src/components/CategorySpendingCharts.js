import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const CategorySpendingCharts = ({ expenses }) => {
  const pieRef = useRef(null);
  const barRef = useRef(null);
  const legendRef = useRef(null);
  const containerRef = useRef(null);
  const tooltipRef = useRef(null);

  useEffect(() => {
    if (!expenses || expenses.length === 0) return;

    const data = expenses.map((expense) => ({
      category: expense.category,
      value: expense.totalAmount,
      share: expense.percentageShare,
    }));

    /*** ✅ PIE CHART ***/
    const pieWidth = 280;
    const pieHeight = 300;
    const pieRadius = Math.min(pieWidth, pieHeight) / 2;

    d3.select(pieRef.current).selectAll("*").remove();
    d3.select(legendRef.current).selectAll("*").remove();

    const categories = Array.from(new Set(expenses.map(d => d.category)));

    const pieSvg = d3
      .select(pieRef.current)
      .attr("width", pieWidth + 150)
      .attr("height", pieHeight)
      .append("g")
      .attr("transform", `translate(${pieWidth / 2}, ${pieHeight / 2})`);

    const colorScale = d3.scaleOrdinal(d3.schemeCategory10);
    const pie = d3.pie().value((expense) => expense.value).sort(null);
    const arc = d3.arc().innerRadius(0).outerRadius(pieRadius);
    const arcHover = d3.arc().innerRadius(0).outerRadius(pieRadius + 10);
    const pieData = pie(data);

    /*** ✅ Tooltip Setup ***/
    const tooltip = d3.select(tooltipRef.current)
      .style("position", "absolute")
      .style("background", "#fff")
      .style("border", "1px solid #ccc")
      .style("padding", "8px")
      .style("border-radius", "5px")
      .style("box-shadow", "0px 4px 6px rgba(0,0,0,0.2)")
      .style("display", "none")
      .style("pointer-events", "none")
      .style("z-index", "9999");

    /*** ✅ Draw Pie Chart with Tooltip ***/
    pieSvg
      .selectAll(".arc")
      .data(pieData)
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

    const legend = d3.select(legendRef.current);
    legend
      .selectAll(".legend-item")
      .data(data)
      .enter()
      .append("div")
      .attr("class", "legend-item")
      .style("display", "flex")
      .style("align-items", "center")
      .style("margin-bottom", "5px")
      .style("margin-left", "1px")
      .html(
        (expense) =>
          `<div style="width: 5px; height: 20px; background: ${colorScale(
            expense.category
          )}; margin-right: 10px;"></div>${expense.category}`
      );

    /*** ✅ BAR CHART - ADJUSTED WIDTH TO FIT MODAL ***/
    const modal = document.querySelector(".modal-dialog");
    const modalWidth = modal ? modal.clientWidth : 900; // Fallback width if modal is not found
  
    // Adjust bar chart width to fit within the modal
    const barWidth = Math.min(modalWidth - 100, categories.length * 60); // Ensure it fits inside modal
    const barHeight = 400;
    const margin = { top: 20, right: 30, bottom: 70, left: 40 }; // Increased bottom margin for labels
  
    const chartWidth = barWidth - margin.left - margin.right;
    const chartHeight = barHeight - margin.top - margin.bottom;
  
    // Create xScale with adjusted width
    const xScale = d3
      .scaleBand()
      .domain(categories)
      .range([0, chartWidth])
      .padding(0.3);
  
    // Create yScale
    const yScale = d3
      .scaleLinear()
      .domain([0, d3.max(data, (expense) => expense.value)])
      .nice()
      .range([chartHeight, 0]);
  
    // Clear previous bar chart
    d3.select(barRef.current).selectAll("*").remove();
  
    // Create SVG for bar chart
    const barSvg = d3
      .select(barRef.current)
      .attr("width", barWidth)
      .attr("height", barHeight)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);
  
    // Add x-axis
    barSvg
      .append("g")
      .attr("transform", `translate(0, ${chartHeight})`)
      .call(d3.axisBottom(xScale))
      .selectAll("text")
      .attr("transform", "rotate(-20)") // Rotate labels for better readability
      .style("text-anchor", "end");
  
    // Add y-axis
    barSvg.append("g").call(d3.axisLeft(yScale));
  
    // Draw bars
    barSvg
      .selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", (expense) => xScale(expense.category))
      .attr("y", (expense) => yScale(expense.value))
      .attr("width", xScale.bandwidth())
      .attr("height", (expense) => chartHeight - yScale(expense.value))
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
    <div className="visualization-container">
      <div ref={containerRef} className="pie-and-legend-container">
        <div>
          <h5>Pie Chart</h5>
          <svg ref={pieRef} style={{ marginTop: "25px" }}></svg>
        </div>
        <div ref={legendRef} className="legend-container"></div>
      </div>
      <div className="chart">
        <h5>Bar Chart</h5>
        <svg ref={barRef}></svg>
      </div>
      <div ref={tooltipRef} className="d3-tooltip"></div>
    </div>
  );
};

export default CategorySpendingCharts;
