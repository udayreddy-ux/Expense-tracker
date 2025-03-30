import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";
import { useAuth } from "../components/AuthContext";
import { Modal, Button} from "react-bootstrap";
import OtpInput from "react-otp-input";
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';

const Profile = () => {
    const {logout}=useAuth();
    const navigate=useNavigate();
    //const [user, setUser] = useState({});
    const [errors,setErrors]=useState({});
    const [showConfirm,setShowConfirm]=useState(false);
    const [otp,setOtp]=useState('');
    const [showOtp,setOtpModal]=useState(false);
    //const [phone, setPhone] = useState('');
    
    const [formData, setFormData] = useState({
        first_name: "",
        last_name: "",
        email: "",
        mobile: "",
    });

    //const [updatedUser,updateUser]=useState({
    //    first_name: "",
    //    last_name: "",
    //    email: "",
    //    mobile: "",
    //});

    const validateForm = () => {
        const newErrors = {};
        //const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    
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
                setShowConfirm(false);
            })
            .catch((error) => console.error("Error fetching User details", error));
    };

    const handleSubmit = async (e) => {
        if (e) e.preventDefault();
        if(validateForm()){
            try
            {
                await API.put(`users/updateProfile`,formData)
                alert("Profile update sucessfully, please login again with valid credentials");
                logout();
                setShowConfirm(false);
            }
            catch(err){
                alert(err.response?.data || "OTP is valid for 5 minutes. Enter the existing one or try again later.");

            }
        }
    };

    const handleProfileUpdates = (e)=>{
        e.preventDefault();
        if (validateForm()) setShowConfirm(true);
    }

    const handleOtpSubmit = async () => {
        const otpString=String(otp);
        if(otpString.length===6){
            try{
                const response=await API.post(`auth/verify`,{otp:otpString,email:formData.email});
                alert(response.data);
                console.log(response);
                if(response.data==="OTP verified successfully."){
                    setOtpModal(false);
                    handleSubmit();
                    
                }
                if(response.data==="Too many failed attempts. Try again later."){
                    setOtpModal(false);
                    navigate("/Profile");
                }
            } catch(error){
                console.error("Verification failed, try again",error);
                navigate("/Profile");
            }
        }
        else{
            alert("Enter a valid 6-digit OTP.");
        }
    }

    const handleConfirmUpdate = async (e) =>{
        setShowConfirm(false);
        setOtpModal(true);
        try{
            const response = await API.post(`/auth/send/email`,{email:formData.email});
            alert(response.data);
        }
        catch (err) {
            alert(err.response?.data || "Too many invalid attempts, Please Try again!!")
            navigate("/Profile")
        }
    }

    const handlePhoneChange = (value) => {
        setPhone(value);
        setFormData((previousFormData)=>({
            ...previousFormData,
            mobile: value.startsWith("+") ? value : `+${value}`,
        }));
    }

    return (
        <div className="bg-gradient-custom">
            <div className="container m-5">
                <h2 className="mb-4 text-center analytics-dashboard-heading" style={{fontSize: "30px"}}>Edit Personal Information</h2>
                <form onSubmit={handleProfileUpdates} noValidate>
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
                        <PhoneInput
                                country={'us'} 
                                value={formData.mobile}
                                onChange={handlePhoneChange}
                                //containerStyle={{ width: "110px" }}
                                inputStyle={{ width: "100%" }}
                            />
                        {errors.mobile && <div className="invalid-feedback">{errors.mobile}</div>}
                    </div>

                    <button type="submit" className="btn btn-secondary" style={{marginTop:"10px"}}>
                        Update
                    </button>
                </form>

                {/*Confirm Updatation of Details*/}
                <Modal show={showConfirm} onHide={()=> setShowConfirm(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>Confirm Updation</Modal.Title>
                    </Modal.Header>
                    <Modal.Body> Are you sure with the changes you have made to your profile?
                    <p style={{ fontSize: "14px", marginTop: "15px", color: "#555" }}>
                            <strong>Note:</strong>
                            Profile updates require re-login for verification.
                        </p>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="success" onClick={handleConfirmUpdate}>
                            Update
                        </Button>
                        <Button variant="dark" onClick={()=>setShowConfirm(false)}>
                            Cancel
                        </Button> 
                    </Modal.Footer>
                </Modal>
                <Modal show={showOtp} onHide={()=>setOtpModal(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>Enter OTP</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <p>Please enter the OTP sent to your email/mobile.</p>
                        <OtpInput value={otp} onChange={setOtp} 
                        numInputs={6} renderSeparator={<span>-</span>} 
                        renderInput={(props)=><input {...props} className="otp-input"/>}
                        />
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="primary" onClick={handleOtpSubmit}>
                            Verify & Update
                        </Button>
                        <Button variant="dark" onClick={()=>setOtpModal(false)}>
                            Cancel
                        </Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </div>
    );
};

export default Profile;
