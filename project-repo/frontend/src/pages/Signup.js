import React, { useState } from "react";
import { use } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import axios from "axios";
import BASE_URL from "../config";
const SignUp = () => {
    const navigate=useNavigate();

    const goToAboutPage=()=>{
        navigate('/');
    }

    const [formData,setFormData]=useState({
        first_name: "",
        last_name: "",
        email: "",
        mobile: "",
        password: "",
        confirmPassword:"",
    });

    const [errors,setErrors]=useState({});

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const validateForm = () => {
        const newErrors = {};
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    
        if (!formData.first_name.trim()) {
            newErrors.first_name = "First name is required.";
        }
        if (!formData.last_name.trim()) {
            newErrors.last_name = "Last name is required.";
        }
        if (!formData.email.trim()) {
            newErrors.email = "Email is required.";
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = "Invalid email format.";
        }
        if (!formData.mobile.trim()) {
            newErrors.mobile = "Mobile number is required.";
        } else if (!/^\d{10}$/.test(formData.mobile)) {
            newErrors.mobile = "Phone number must be 10 digits.";
        }
        if (!formData.password.trim()) {
            newErrors.password = "Password is required.";
        } else if (!passwordRegex.test(formData.password)) {
            newErrors.password = "Password must be at least 8 characters long and include at least one letter and one number, and one special character.";
        }
        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Passwords do not match.";
        }
    
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    

    const handleSubmit = async (e) => {
        e.preventDefault();
        if(validateForm()){
            try 
            {
                const response = await axios.post(`${BASE_URL}/signup`, formData);
                alert(response.data); // Success message from backend
                navigate("/signIn"); // Redirect to login page
            } 
            catch (err) {
                alert(err.response?.data || "An error occurred");
            }
        }
    };
    return(
        <div className="bg-gradient-custom">
            <div className="container m-5">
                <h2 className="mb-4" style={{textAlign:"center"}}>Sign Up</h2>
                <form onSubmit={handleSubmit} noValidate>
                {/* First Name */}
                <div className="mb-3">
                    <label htmlFor="first_name" className="form-label" style={{fontSize:20}}>
                        First Name
                    </label>
                    <input
                        type="text"
                        className={`form-control ${errors.first_name ? "is-invalid" : ""}`}
                        id="first_name"
                        name="first_name"
                        value={formData.first_name}
                        onChange={handleChange}
                        placeholder="Enter your first name"
                    />
                    {errors.first_name && <div className="invalid-feedback">{errors.first_name}</div>}
                </div>
    
    
                {/* Last Name */}
                <div className="mb-3">
                    <label htmlFor="last_name" className="form-label" style={{fontSize:20}}>
                        Last Name
                    </label>
                    <input
                        type="text"
                        className={`form-control ${errors.last_name ? "is-invalid" : ""}`}
                        id="last_name"
                        name="last_name"
                        value={formData.last_name}
                        onChange={handleChange}
                        placeholder="Enter your last name"
                    />
                    {errors.last_name && <div className="invalid-feedback">{errors.last_name}</div>}
                </div>
    
    
                {/* Email */}
                <div className="mb-3">
                    <label htmlFor="email" className="form-label" style={{fontSize:20}}>
                        Email
                    </label>
                    <input
                        type="email"
                        className={`form-control ${errors.email ? "is-invalid" : ""}`}
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Enter your email"
                    />
                     {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                </div>
    
    
                 {/* Mobile Number */}
                 <div className="mb-3">
                    <label htmlFor="mobile" className="form-label" style={{fontSize:20}}>
                        Phone Number
                    </label>
                    <input
                        type="text"
                        className={`form-control ${errors.mobile ? "is-invalid" : ""}`}
                        id="mobile"
                        name="mobile"
                        value={formData.mobile}
                        onChange={handleChange}
                        placeholder="Enter your mobile number"
                    />
                     {errors.mobile && <div className="invalid-feedback">{errors.mobile}</div>}
                </div>
    
                 {/* Password */}
                 <div className="mb-3">
                    <label htmlFor="password" className="form-label" style={{fontSize:20}}>
                        Password
                    </label>
                    <input
                        type="password"
                        className={`form-control ${errors.password ? "is-invalid" : ""}`}
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Set password"
                    />
                    {errors.password && <div className="invalid-feedback">{errors.password}</div>}
                </div>
    
    
                 {/* Confirm Password */}
                 <div className="mb-3">
                    <label htmlFor="confirmPassword" className="form-label" style={{fontSize:20}}>
                        Confirm Password
                    </label>
                    <input
                        type="password"
                        className={`form-control ${errors.confirmPassword ? "is-invalid" : ""}`}
                        id="confirmPassword"
                        name="confirmPassword"
                        value={formData.confirmPassword}
                        onChange={handleChange}
                        placeholder="Confirm your password"
                    />
                     {errors.confirmPassword && (
                      <div className="invalid-feedback">{errors.confirmPassword}</div>)}
                </div>
    
                <button type="submit" className="btn btn-primary" style={{marginTop:"10px"}}>
                    Sign Up
                </button>
                <button type="cancel" className="btn btn-danger" style={{marginTop:"10px",marginLeft:"30px"}} onClick={()=>goToAboutPage()}>
                    Cancel
                </button>
            </form>
            </div> 
        </div>
    )
}


export default SignUp;