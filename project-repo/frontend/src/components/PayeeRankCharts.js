import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const PayeeRankCharts = ({ expenses }) => {
  const horBarChart = useRef(null);
  const heatMap = useRef(null);
  const tooltipRef = useRef(null);
  const containerRef = useRef(null);

  useEffect(() => {
    if (!expenses || expenses.length === 0) return;

    const data = expenses.map((expense) => ({
      payee: expense.payee,
      payeeRank: expense.payeerank,
      totalAmount: expense.totalAmount,
      percentageShare: expense.percentageShare,
    }));

    const payees = Array.from(new Set(expenses.map((d) => d.payee)));
    const ranks = Array.from(new Set(expenses.map((d) => d.payeeRank))).sort((a, b) => a - b);

    const colorScale = d3.scaleOrdinal(d3.schemeCategory10).domain(payees);
    const modal = document.querySelector(".modal-dialog");
    const modalWidth = modal ? modal.clientWidth : 900;

    /*** ✅ Tooltip Setup ***/
    d3.select(tooltipRef.current).selectAll("*").remove();
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

    /*** ✅ Horizontal Bar Chart ***/
    const barWidth = Math.min(modalWidth - 200, Math.max(payees.length * 90, 200));
    const barHeight = 400;
    const margin = { top: 20, right: 30, bottom: 70, left: 100 };

    d3.select(horBarChart.current).selectAll("*").remove();

    const horBarsvg = d3
      .select(horBarChart.current)
      .attr("width", barWidth)
      .attr("height", barHeight)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    const xScale = d3
      .scaleLinear()
      .domain([0, d3.max(data, (expense) => expense.totalAmount) || 1])
      .nice()
      .range([0, barWidth - margin.left - margin.right]);

    const yScale = d3
      .scaleBand()
      .domain(payees)
      .range([0, barHeight - margin.top - margin.bottom])
      .padding(0.3);

    horBarsvg.append("g")
      .attr("transform", `translate(0, ${barHeight - margin.top - margin.bottom})`)
      .call(d3.axisBottom(xScale).ticks(5));

    horBarsvg.append("g").call(d3.axisLeft(yScale));

    horBarsvg
      .selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", 0)
      .attr("y", (expense) => yScale(expense.payee))
      .attr("width", (expense) => xScale(expense.totalAmount))
      .attr("height", yScale.bandwidth())
      .attr("fill", (expense) => colorScale(expense.payee))
      .on("mouseover", function (event, expense) {
        d3.select(this).attr("stroke", "black").attr("stroke-width", 2);

        const rect = event.target.getBoundingClientRect();
        const modalRect = containerRef.current.getBoundingClientRect();
        const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10;
        const topPos = rect.top - modalRect.top + modal.scrollTop - 10;

        tooltip
          .style("display", "block")
          .html(
            `<strong>Payee:</strong> ${expense.payee}<br/>
             <strong>Amount:</strong> ${expense.totalAmount}<br/>
             <strong>Rank:</strong> ${expense.payeeRank}`
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
    <div className="visualization-container" ref={containerRef} style={{ display: "flex", gap: "30px", flexDirection: "column", alignItems: "center" }}>
      <div className="chart">
        <h5>Payee Rank - Horizontal Bar Chart</h5>
        <svg ref={horBarChart}></svg>
      </div>

      <div ref={tooltipRef} className="d3-tooltip"></div>
    </div>
  );
};

export default PayeeRankCharts;
