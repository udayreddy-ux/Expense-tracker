import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const CategoryMonthlyCharts = ({ expenses }) => {
  const stackedBarChartRef = useRef(null);
  const heatmapRef = useRef(null);
  const tooltipRef = useRef(null);

  useEffect(() => {
    if (!expenses || expenses.length === 0) return;

    //  Data Processing
    const months = Array.from(new Set(expenses.map(d => d.monthName)));
    const categories = Array.from(new Set(expenses.map(d => d.category)));

    //  Transform Data for Stacked Chart
    const stackedData = d3.groups(expenses, d => d.monthName).map(([month, entries]) => {
      let obj = { month };
      categories.forEach(category => {
        obj[category] = entries.find(d => d.category === category)?.totalAmount || 0;
      });
      return obj;
    });

    //  Set Dynamic Chart Dimensions 
    const containerWidth = Math.max(600, months.length * 70);
    const height = 400;
    const margin = { top: 20, right: 30, bottom: 60, left: 60 };

    const xScale = d3.scaleBand().domain(months).range([0, containerWidth - margin.left - margin.right]).padding(0.3);
    const yScale = d3.scaleLinear().domain([0, d3.max(stackedData, d => d3.sum(categories, key => d[key]))]).nice()
      .range([height - margin.top - margin.bottom, 0]);

    const colorScale = d3.scaleOrdinal(d3.schemeCategory10).domain(categories);

    //  Tooltip Setup
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

    // Stacked Bar Chart 
    d3.select(stackedBarChartRef.current).selectAll("*").remove();
    const barSvg = d3.select(stackedBarChartRef.current)
      .attr("width", containerWidth)
      .attr("height", height)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    barSvg.append("g")
      .attr("transform", `translate(0, ${height - margin.top - margin.bottom})`)
      .call(d3.axisBottom(xScale));

    barSvg.append("g").call(d3.axisLeft(yScale));

    const stack = d3.stack().keys(categories)(stackedData);

    barSvg.selectAll(".layer")
      .data(stack)
      .enter().append("g")
      .attr("class", "layer")
      .attr("fill", d => colorScale(d.key))
      .selectAll("rect")
      .data(d => d)
      .enter().append("rect")
      .attr("x", d => xScale(d.data.month))
      .attr("y", d => yScale(d[1]))
      .attr("height", d => yScale(d[0]) - yScale(d[1]))
      .attr("width", xScale.bandwidth())
      .on("mouseover", function (event, d) {
        d3.select(this).attr("stroke", "black").attr("stroke-width", 2);

        // Get Modal Position Instead of Page
        const modal = document.querySelector(".modal-dialog");
        const modalRect = modal.getBoundingClientRect();
        const rect = event.target.getBoundingClientRect();

        // Calculate Position Relative to Modal
        const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10;
        const topPos = rect.top - modalRect.top + modal.scrollTop - 10;

        tooltip.style("display", "block")
          .html(`
            <strong>Category:</strong> ${d3.select(this.parentNode).datum().key}<br/>
            <strong>Month:</strong> ${d.data.month}<br/>
            <strong>Amount:</strong> ${(d[1] - d[0]).toFixed(2)}
          `)
          .style("left", `${leftPos}px`)
          .style("top", `${topPos}px`);
      })
      .on("mouseout", function () {
        d3.select(this).attr("stroke", "none");
        tooltip.style("display", "none");
      });

    // Heatmap
    d3.select(heatmapRef.current).selectAll("*").remove();
    const heatSvg = d3.select(heatmapRef.current)
      .attr("width", containerWidth)
      .attr("height", height)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    const heatColorScale = d3.scaleSequential(d3.interpolateBlues)
      .domain([0, d3.max(expenses, d => d.totalAmount)]);

    heatSvg.selectAll(".cell")
      .data(expenses)
      .enter().append("rect")
      .attr("x", d => xScale(d.monthName))
      .attr("y", d => categories.indexOf(d.category) * 25)
      .attr("width", xScale.bandwidth())
      .attr("height", 25)
      .attr("fill", d => heatColorScale(d.totalAmount))
      .on("mouseover", function (event, d) {
        d3.select(this).attr("stroke", "black").attr("stroke-width", 2);

        //Tooltip for Heatmap
        const modal = document.querySelector(".modal-dialog");
        const modalRect = modal.getBoundingClientRect();
        const rect = event.target.getBoundingClientRect();

        const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10;
        const topPos = rect.top - modalRect.top + modal.scrollTop - 10;

        tooltip.style("display", "block")
          .html(`
            <strong>Category:</strong> ${d.category}<br/>
            <strong>Month:</strong> ${d.monthName}<br/>
            <strong>Amount:</strong> ${d.totalAmount.toFixed(2)}
          `)
          .style("left", `${leftPos}px`)
          .style("top", `${topPos}px`);
      })
      .on("mouseout", function () {
        d3.select(this).attr("stroke", "none");
        tooltip.style("display", "none");
      });

  }, [expenses]);

  return (
    <div className="visualization-container" style={{ display: "flex", gap: "30px" }}>
      <div className="chart">
        <h5>Stacked Bar Chart</h5>
        <svg ref={stackedBarChartRef}></svg>
      </div>
      <div className="chart">
        <h5>Heatmap</h5>
        <svg ref={heatmapRef}></svg>
      </div>
      <div ref={tooltipRef} className="d3-tooltip"></div>
    </div>
  );
};

export default CategoryMonthlyCharts;
