import React from "react";
import { Button } from 'react-bootstrap';
import {useNavigate} from "react-router-dom";
const About=()=>{
    const navigate = useNavigate();

    const handleGetStarted = () => {
    navigate("/SignUp"); 
    };
    return(
        <div className="bg-gradient-custom vh-100 d-flex flex-column justify-content-center align-items-center">
            <h1 className="text-white display-4" style={{fontWeight:"bold"}}>Welcome to Expense Tracker</h1>
            <img src="expense_tracker_img.png" alt="Expense Tracker"className="img-fluid" style={{maxWidth:"80%",maxHeight:"400px",}}/>
            <p style={{fontSize:"1rem",fontFamily:"cursive",marginBottom:"20px",}}>Manage your finances effortlessly. Track your expenses, set budgets, and analyze your spending patterns with advanced security features.</p>
            <Button variant="primary" size="lg" onClick={()=>handleGetStarted()}>Get started</Button>
        </div>
    ); 
}
export default About;