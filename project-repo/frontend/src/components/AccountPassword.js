import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import BASE_URL from "../config";

const AccountPassword = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleResetPassword = async (e) => {
        e.preventDefault();
    
        // Password Validation Regex
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    
        if (!newPassword || !confirmPassword) {
            setError("Password fields cannot be empty!");
            return;
        }
    
        if (!passwordRegex.test(newPassword)) {
            setError("Password must be at least 8 characters long and include at least one letter and one number, and one special character.");
            return;
        }
    
        if (newPassword !== confirmPassword) {
            setError("Passwords do not match!");
            return;
        }
    
        try {
            const response = await axios.post(`${BASE_URL}/reset-password`, {
                token,
                newPassword,
            });
            alert(response.data); // Success message
            navigate("/signIn"); // Redirect to login
        } catch (err) {
            setError(err.response?.data || "Token expired, please use forgot password again!!");
        }
    };    

    return (
        <div className="bg-reset-custom">
            <div className="reset-password-card">
                <h2>Reset Password</h2>
                {error && <div className="error">{error}</div>}
                <form onSubmit={handleResetPassword}>
                    <div>
                        <label>New Password:</label>
                        <input
                            type="password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            placeholder="Enter new password"
                        />
                    </div>
                    <div>
                        <label>Confirm Password:</label>
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="Confirm new password"
                        />
                    </div>
                    <button type="submit">Reset Password</button>
                    <button
                        type="button"
                        className="close-btn"
                        onClick={() => navigate("/signIn")}
                    >
                        Cancel
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AccountPassword;
