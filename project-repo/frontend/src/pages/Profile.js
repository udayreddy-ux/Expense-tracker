import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";
import BASE_URL from "../config";
import { useAuth } from "../components/AuthContext";
import axios from "axios";

const Profile = () => {
    const {isAuthenticated, logout}=useAuth();
    const navigate=useNavigate();
    const [user, setUser] = useState({});
    const [errors,setErrors]=useState({});
    const [formData, setFormData] = useState({
        first_name: "",
        last_name: "",
        email: "",
        mobile: "",
    });

    const [updatedUser,updateUser]=useState({
        first_name: "",
        last_name: "",
        email: "",
        mobile: "",
    });


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
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    
    const handleChange = (e) =>{
        setFormData({
            ...formData,
            [e.target.name] : e.target.value,
        });
    };

    useEffect(() => {
        fetchUserDetails();
    }, []);

    const fetchUserDetails = () => {
        API.get(`/users/Profile`)
            .then((response) => {
                console.log("Backend Response:", response.data);
                setUser(response.data || {});
                setFormData({
                    first_name: response.data.first_name || "",
                    last_name: response.data.last_name || "",
                    email: response.data.email || "",
                    mobile: response.data.mobile || "",
                });
                updateUser({
                    first_name: response.data.first_name || "",
                    last_name: response.data.last_name || "",
                    email: response.data.email || "",
                    mobile: response.data.mobile || "",
                });
            })
            .catch((error) => console.error("Error fetching User details", error));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if(validateForm()){
            try
            {
                const response = await API.put(`users/updateProfile`,formData)
                alert("Profile update sucessfully, please login again with valid credentials");
                logout();
            }
            catch(err){
                alert(err.response?.data || "An error occurred");
            }
        }
    };

    return (
        <div className="bg-gradient-custom">
            <div className="container m-5">
                <h2 className="mb-4 text-center analytics-dashboard-heading" style={{fontSize: "30px"}}>Edit Personal Information</h2>
                <form onSubmit={handleSubmit} noValidate>
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
                        />
                        {errors.first_name && <div className="invalid-feedback">{errors.first_name}</div>}
                    </div>
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
                        />
                        {errors.last_name && <div className="invalid-feedback">{errors.last_name}</div>}
                    </div>
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
                        />
                        {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                    </div>
                    <div className="mb-3">
                        <label htmlFor="mobile" className="form-label" style={{ fontSize: 20 }}>
                            Mobile
                        </label>
                        <input
                            type="text"
                            className={`form-control ${errors.mobile ? "is-invalid" : ""} `}
                            id="mobile"
                            name="mobile"
                            value={formData.mobile}
                            onChange={handleChange}
                        />
                        {errors.mobile && <div className="invalid-feedback">{errors.mobile}</div>}
                    </div>

                    <button type="submit" className="btn btn-secondary" style={{marginTop:"10px"}}>
                        Update
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Profile;
