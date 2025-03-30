import React,{useState} from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import BASE_URL from "../config";
import { useAuth } from "../components/AuthContext";
import ResetPassword from "../components/ResetPassword";
const SignIn = () =>{
    const navigate = useNavigate();
    const {login} = useAuth();
    const [showForgotPassword,setShowForgotPassword]=useState(false);
    const [formData,setFormData]=useState({
        email:"",
        password:"",
    });

    const [errors,setErrors]=useState({});
    const [apiError,setApiError]=useState("");

    const handleChange = (e) =>{
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const validateForm = () => {
        const newErrors={};
        if(!formData.email.trim()){
            newErrors.email="Email is required.";
        }else if (!/\S+@\S+\.\S+/.test(formData.email)){
            newErrors.email="Invalid email format.";
        }
        if(!formData.password.trim()){
            newErrors.password="Password is required.";
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length==0;
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
      if(validateForm()){
        try {
          const response = await axios.post(`${BASE_URL}/login`,formData);
          const token = response.data.split("Bearer ")[1]; //Extract token
          login(token);
          alert("Login Sucessful!");
          navigate("/Home");
        } catch(err){
          setApiError(err.response?.data || "An error occurred");
        }
      }
    };

    const handleOpenForgotPassword = () => setShowForgotPassword(true);
    const handleCloseForgotPassword = () => setShowForgotPassword(false);

    return(
        <div className="bg-gradient-custom vh-100 d-flex justify-content-center align-items-center">
        <div className="login-card shadow p-4 bg-white rounded">
          <h2 className="mb-4 text-center">Login</h2>
          {apiError && <div className="alert alert-danger">{apiError}</div>}
          <form className="w-100" onSubmit={handleSubmit} noValidate>
            {/* Email */}
            <div className="mb-3">
              <label htmlFor="email" className="form-label" style={{fontSize:"20px"}}>
                Email
              </label>
              <input
                type="email"
                className={`form-control ${errors.email ? "is-invalid" : ""}`}
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter your mail id"
              />
              {errors.email && <div className="invalid-feedback">{errors.email}</div>}
            </div>
  
            {/* Password */}
            <div className="mb-3">
              <label htmlFor="password" className="form-label" style={{fontSize:"20px"}}>
                Password
              </label>
              <input
                type="password"
                className={`form-control ${errors.password ? "is-invalid" : ""}`}
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter your password"
              />
              {errors.password && <div className="invalid-feedback">{errors.password}</div>}
            </div>
  
            {/* Login Button */}
            <div className="d-flex justify-content-center">
              <button type="submit" className="btn btn-primary w-10">
                Login
              </button>
            </div>
          </form>
          <p className="mt-3 text-center">
            Don't have an account? <a href="/signup">Sign up</a>
          </p>
          <p className="mt-3 text-center">
            <button type="button" className="btn-link" onClick={handleOpenForgotPassword}>Forgot password</button>
          </p>
          <p>
            {showForgotPassword && <ResetPassword showForgotPassword={showForgotPassword} onClose={handleCloseForgotPassword}/>}
          </p>
        </div>
      </div>
    )
}

export default SignIn;