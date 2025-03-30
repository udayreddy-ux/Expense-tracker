import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const MonthlySpendingCharts = ({ expenses, currencies }) => {
  const lineChartRef = useRef(null);
  const stackedBarChartRef = useRef(null);
  const tooltipRef = useRef(null);

  useEffect(() => {
    if (!expenses || expenses.length === 0) return;

    if(!currencies || currencies.length === 0) return;
    
    const months = Array.from(new Set(expenses.map(d => d.monthName)));
    //const categories = Array.from(new Set(expenses.map(d => d.category)));

    const TotalCurrencies = Array.from(new Set(currencies.map(d=> d.currency)));
    const expMonths = Array.from(new Set(currencies.map(d => d.monthName)));
    // Transform data for stacked bar chart
    const stackedData = TotalCurrencies.map((currency) => {
      let obj = {currency};
      expMonths.forEach((month)=>{
        const monthEntry = currencies.find((d)=>d.currency===currency && d.monthName.trim()===month.trim());
        obj[month] = monthEntry ? monthEntry.totalAmount : 0;
      });
      return obj;
    });

    console.log(stackedData);

    // Data for line chart
    const lineChartData = expenses.reduce((acc, curr) => {
      let monthData = acc.find(d => d.month === curr.monthName);
      if (monthData) {
        monthData.value += curr.totalAmount;
      } else {
        acc.push({ month: curr.monthName, value: curr.totalAmount });
      }
      return acc;
    }, []);

    /*** Set Dynamic Chart Dimensions ***/
    const containerWidth = Math.max(550, months.length * 60);
    const containerHeight = 400;
    const margin = { top: 20, right: 30, bottom: 60, left: 60 };

    /*** Create Tooltip ***/
    const tooltip = d3.select(tooltipRef.current)
      .style("position", "absolute")
      .style("background", "#fff")
      .style("border", "1px solid #ccc")
      .style("padding", "8px")
      .style("border-radius", "5px")
      .style("box-shadow", "0px 4px 6px rgba(0,0,0,0.2)")
      .style("display", "none")
      .style("pointer-events", "none")
      .style("z-index", "9999")
      .style("font-size", "14px")
      .style("font-weight", "bold");

    /*** Define X & Y Scales ***/
    const xScale = d3.scaleBand()
      .domain(months)
      .range([0, containerWidth - margin.left - margin.right])
      .padding(0.3);

    const yScale = d3.scaleLinear()
      .domain([0, d3.max(lineChartData, d => d.value) * 1.1])
      .nice()
      .range([containerHeight - margin.top - margin.bottom, 0]);

    //const colorScale = d3.scaleOrdinal(d3.schemeCategory10).domain(categories);

    /***  Clear Old Charts ***/
    d3.select(lineChartRef.current).selectAll("*").remove();

    /***  Line Chart ***/
    const lineSvg = d3.select(lineChartRef.current)
      .attr("width", containerWidth)
      .attr("height", containerHeight)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    lineSvg.append("g")
      .attr("transform", `translate(0, ${containerHeight - margin.top - margin.bottom})`)
      .call(d3.axisBottom(xScale))
      .selectAll("text")
      .attr("transform", "rotate(-45)")
      .style("text-anchor", "end");

    lineSvg.append("g").call(d3.axisLeft(yScale));

    const line = d3.line()
      .x(d => xScale(d.month) + xScale.bandwidth() / 2)
      .y(d => yScale(d.value));

    lineSvg.append("path")
      .datum(lineChartData)
      .attr("fill", "none")
      .attr("stroke", "#007bff")
      .attr("stroke-width", 2)
      .attr("d", line);

    // Tooltip on dots in line chart
    lineSvg.selectAll(".dot")
  .data(lineChartData)
  .enter()
  .append("circle")
  .attr("class", "dot")
  .attr("cx", d => xScale(d.month) + xScale.bandwidth() / 2)
  .attr("cy", d => yScale(d.value))
  .attr("r", 5)
  .attr("fill", "#007bff")
  .on("mouseover", function (event, d) {
    d3.select(this).attr("r", 7).attr("fill", "darkblue"); // Highlight point on hover

    // **Get Modal Position Instead of Page**
    const modal = document.querySelector(".modal-dialog"); // Get modal container
    const modalRect = modal.getBoundingClientRect();
    const rect = event.target.getBoundingClientRect();

    // **Calculate Position Relative to Modal**
    const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10; 
    const topPos = rect.top - modalRect.top + modal.scrollTop - 10; 

    tooltip.style("display", "block")
      .style("opacity", 1)
      .html(`
        <strong>Month:</strong> ${d.month}<br/>
        <strong>Amount:</strong> ${d.value.toFixed(2)}
      `)
      .style("left", `${leftPos}px`)
      .style("top", `${topPos}px`);
  })
  .on("mouseout", function () {
    d3.select(this).attr("r", 5).attr("fill", "#007bff"); // Reset size on mouse out
    tooltip.style("display", "none");
  });


    /*** Stacked Bar Chart ***/

    const stackxScale = d3.scaleBand().domain(TotalCurrencies).range([0, containerWidth - margin.left - margin.right]).padding(0.3);
    const stackyScale = d3.scaleLinear().domain([0,d3.max(stackedData,d=>d3.sum(expMonths, key => d[key]))]).nice()
      .range([containerHeight - margin.top - margin.bottom, 0]);
    
    const stackColorScale = d3.scaleOrdinal(d3.schemeCategory10).domain(expMonths); 

    d3.select(stackedBarChartRef.current).selectAll("*").remove();

    const barSvg = d3.select(stackedBarChartRef.current)
      .attr("width", containerWidth)
      .attr("height", containerHeight)
      .append("g")
      .attr("transform", `translate(${margin.left}, ${margin.top})`);

    barSvg.append("g")
      .attr("transform", `translate(0, ${containerHeight - margin.top - margin.bottom})`)
      .call(d3.axisBottom(stackxScale))
      .selectAll("text")
      .attr("transform", "rotate(-45)")
      .style("text-anchor", "end");

    barSvg.append("g").call(d3.axisLeft(stackyScale));

    const stack = d3.stack().keys(expMonths)(stackedData);
    console.log(stack);
    barSvg.selectAll(".layer")
      .data(stack)
      .enter().append("g")
      .attr("class", "layer")
      .attr("fill", d => stackColorScale(d.key))
      .selectAll("rect")
      .data(d => d)
      .enter().append("rect")
      .attr("x", d => stackxScale(d.data.currency))
      .attr("y", d => stackyScale(d[1]))
      .attr("height", d => stackyScale(d[0]) - stackyScale(d[1]))
      .attr("width", stackxScale.bandwidth())
      .on("mouseover", function (event, d) {
        d3.select(this).attr("stroke", "black").attr("stroke-width", 2);
        
        // **Get Modal Position Instead of Page**
        const modal = document.querySelector(".modal-dialog"); // Get modal container
        const modalRect = modal.getBoundingClientRect();
        const rect = event.target.getBoundingClientRect();

        // **Calculate Position Relative to Modal**
        const leftPos = rect.left - modalRect.left + modal.scrollLeft + 10; 
        const topPos = rect.top - modalRect.top + modal.scrollTop - 10; 

        tooltip.style("display", "block")
          .style("opacity", 1)
          .html(`
            <strong>Currency:</strong> ${d.data.currency}<br/>
            <strong>Month:</strong> ${d3.select(this.parentNode).datum().key}<br/>
            <strong>Amount:</strong> ${(d[1] - d[0]).toFixed(2)}
          `)
          .style("left", `${leftPos}px`)
          .style("top", `${topPos}px`);
      })
      .on("mouseout", function () {
        d3.select(this).attr("stroke", "none");
        tooltip.style("display", "none");
      });

  }, [expenses,currencies]);

  return (
    <div className="visualization-container">
      <div className="chart">
        <h5>Monthly Spending (Line Chart)</h5>
        <svg ref={lineChartRef}></svg>
      </div>
      <div className="chart">
        <h5>Monthly Spending (Stacked Bar Chart)</h5>
        <svg ref={stackedBarChartRef}></svg>
      </div>
      <div ref={tooltipRef} className="tooltip"></div>
    </div>
  );
};

export default MonthlySpendingCharts;
