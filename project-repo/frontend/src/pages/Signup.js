import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import BASE_URL from "../config";
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';

const SignUp = () => {
    const navigate = useNavigate();
    const [phone, setPhone] = useState('');
    const [isChecked, setIsChecked] = useState(false); // State for checkbox

    const goToAboutPage = () => {
        navigate('/');
    };

    const [formData, setFormData] = useState({
        first_name: "",
        last_name: "",
        email: "",
        mobile: "",
        password: "",
        confirmPassword: "",
    });

    const [errors, setErrors] = useState({});

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
        } else if (!/^\+\d{1,3}\d{7,15}$/.test(formData.mobile)) {
            newErrors.mobile = "Enter a valid international phone number (E.164 format).";
        }
        if (!formData.password.trim()) {
            newErrors.password = "Password is required.";
        } else if (!passwordRegex.test(formData.password)) {
            newErrors.password = "Password must be at least 8 characters long and include at least one letter, one number, and one special character.";
        }
        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Passwords do not match.";
        }
        if (!isChecked) {
            newErrors.consent = "You must agree to receive OTPs and emails.";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handlePhoneChange = (value) => {
        setPhone(value);
        setFormData((prevFormData) => ({
            ...prevFormData,
            mobile: value.startsWith("+") ? value : `+${value}`,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const isValid = validateForm();
        if (isValid) {
            try {
                const response = await axios.post(`${BASE_URL}/signup`, formData);
                alert(response.data); // Success message from backend
                navigate("/signIn"); // Redirect to login page
            } catch (err) {
                alert(err.response?.data || "An error occurred");
            }
        }
    };

    return (
        <div className="bg-gradient-custom">
            <div className="container m-5">
                <h2 className="mb-4 text-center">Sign Up</h2>
                <form onSubmit={handleSubmit} noValidate>
                    {/* First Name */}
                    <div className="mb-3">
                        <label htmlFor="first_name" className="form-label" style={{ fontSize: 20 }}>
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
                        <label htmlFor="last_name" className="form-label" style={{ fontSize: 20 }}>
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
                        <label htmlFor="email" className="form-label" style={{ fontSize: 20 }}>
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
                        <label htmlFor="mobile" className="form-label" style={{ fontSize: 20 }}>
                            Phone Number
                        </label>
                        <PhoneInput
                            country={'us'}
                            onChange={handlePhoneChange}
                            inputStyle={{ width: "100%" }}
                        />
                        {errors.mobile && <div className="invalid-feedback d-block">{errors.mobile}</div>}
                    </div>

                    {/* Password */}
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label" style={{ fontSize: 20 }}>
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
                        <label htmlFor="confirmPassword" className="form-label" style={{ fontSize: 20 }}>
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
                        {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword}</div>}
                    </div>

                    {/* Consent Checkbox */}
                    <div className="mb-3 form-check">
                        <input
                            type="checkbox"
                            className={`form-check-input ${errors.consent ? "is-invalid" : ""}`}
                            id="consentCheckbox"
                            checked={isChecked}
                            onChange={() => setIsChecked(!isChecked)}
                        />
                        <label className="form-check-label" htmlFor="consentCheckbox">
                            I agree to receive OTPs and email notifications.
                        </label>
                        {errors.consent && <div className="invalid-feedback d-block">{errors.consent}</div>}
                    </div>

                    <button type="submit" className="btn btn-primary">Sign Up</button>
                    <button type="button" className="btn btn-danger ms-3" onClick={goToAboutPage}>Cancel</button>
                </form>
            </div>
        </div>
    );
};

export default SignUp;
