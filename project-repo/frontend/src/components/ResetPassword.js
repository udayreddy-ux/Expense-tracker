import React from "react";
import { useState } from "react";
import { Button, Form, Modal} from "react-bootstrap";
import axios from "axios";
import BASE_URL from "../config";
import {useNavigate } from "react-router-dom";
const ResetPassword=({showForgotPassword,onClose})=>{
    const[formData,setFormData]=useState({
        email:"",
    });

    const navigate=useNavigate();

    const[errors,setErrors]=useState({});
    const validateForm=() => {
        const newErrors={};
        if(!formData.email.trim()){
            newErrors.email="Email is required.";
        }else if (!/\S+@\S+\.\S+/.test(formData.email)){
            newErrors.email="Invalid email format.";
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length==0;
    };

    const handleChange=(e)=>{
        setFormData({
            ...formData,
            [e.target.name]:e.target.value,
        });
    };

    const handleForgotPassword = async (e) =>{
        e.preventDefault();
        if(validateForm()){
            try 
            {
                const response = await axios.post(`${BASE_URL}/forgot-password`, formData);
                alert(response.data); // Success message from backend
                navigate("/signIn"); // Redirect to login page
                onClose();
            } 
            catch (err) {
                alert(err.response?.data || "An error occurred");
            }
        }
    };
    return(
        <Modal show={showForgotPassword} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>
                    Reset Password
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                        <Form.Label>Email address</Form.Label>
                        <Form.Control
                            type="email"
                            name="email"
                            className={`form-control ${errors.email ? "is-invalid" : ""}`}
                            id="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Enter your email"
                            autoFocus/>
                            {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant = "primary" onClick={handleForgotPassword}>
                    Continue
                </Button>

                <Button variant="secondary" onClick={onClose}>
                    Close
                </Button>

            </Modal.Footer>
        </Modal>
    )
}
export default ResetPassword;