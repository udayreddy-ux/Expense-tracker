export const visualizations=[
    {
        title:"Category-Wise spending",
        description:"Shows a detailed breakdown of total spending and the percentage share of each category within the chosen currency type, offering deeper insights into spending patterns.",
        icon:"📊",
        chartype:"Pie Chart, Bar Chart",
        val: "categorySpending"
    },
    {
        title:"Month-Wise Spending Analysis",
        description:"Displays the total spending breakdown for each month across the selected year, providing a clear view of monthly expenditure trends.",
        icon:"📈",
        chartype:"Stacked Bar, Line Chart",
        val: "monthlySpending"
    },
    {
        title:"Month-Wise Category Spending",
        description:"Displays each category’s individual contribution to the overall spending for the selected month, providing a clear view of category-specific expenses.",
        icon:"📉",
        chartype:"Stacked Bar, Heat map",
        val: "CategoryMonthlyDistribution"
    },
    {
        title:"Total Spending and Average Share by Category in a Month",
        description:"Highlights the average contribution of each category to the overall spending, offering detailed insights into spending.",
        icon:"🧮",
        chartype:"Doughnut, Bar Chart",
        val: "totalAverageByCategory"
    },
    {
        title:" Top payees and analyze spending patterns for each payee.",
        description:"Displays rankings for payees by the total amount paid in the selected currency, providing a clear analysis of expenses.",
        icon:"📊",
        chartype:"Horizontal Bar, TreeMap",
        val: "payeeRanking"
    }
]

export default visualizations;