import React, { useEffect } from "react";
import './App.css';
import About from "./pages/About";
import SignIn from "./pages/Signin";
import SignUp from "./pages/Signup";
import Home from "./pages/Home";
import Analytics from "./pages/Analytics";
import Expenses from "./pages/Expenses";
import Profile from "./pages/Profile";
import { Routes, Route, Link, Navigate } from 'react-router-dom';
import { FaChartLine, FaExchangeAlt, FaFileExport, FaSignOutAlt, FaHome, FaSignInAlt } from 'react-icons/fa';
import { useAuth } from "./components/AuthContext";
import AccountPassword from "./components/AccountPassword";
import { jwtDecode } from "jwt-decode";

function App() {
    const { isAuthenticated, logout } = useAuth();

    useEffect(() => {
        const checkTokenExpiry = () => {
            const token = localStorage.getItem("token");
            if (!token) return;

            try {
                const decoded = jwtDecode(token);
                const currentTime = Date.now() / 1000;
                if (decoded.exp < currentTime) {
                    alert("Session expired. Logging out.");
                    logout();
                }
            } catch (error) {
                console.error("Invalid token");
                logout();
            }
        };

        const interval = setInterval(checkTokenExpiry, 60000); // Check every minute
        return () => clearInterval(interval);
    }, [logout]);

    return (
        <>
            <nav className='navbar navbar-expand-lg custom-navbar'>
                <div className='container-fluid'>
                    <Link className='navbar-brand' to="/">ExpenSage</Link>
                    <button
                        className="navbar-toggler"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarNav"
                        aria-controls="navbarNav"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    ></button>
                    <div className='collapse navbar-collapse' id="navbarNav">
                        <ul className='navbar-nav ms-auto'>
                            {!isAuthenticated ? (
                                <>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/">
                                            <FaHome style={{ marginRight: "8px" }} />Home
                                        </Link>
                                    </li>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/signIn">
                                            <FaSignInAlt style={{ marginRight: "8px" }} />Login
                                        </Link>
                                    </li>
                                </>
                            ) : (
                                <>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/Home">
                                            <FaHome style={{ marginRight: "8px" }} />Home
                                        </Link>
                                    </li>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/Dashboard">
                                            <FaExchangeAlt style={{ marginRight: "8px" }} />Expenses
                                        </Link>
                                    </li>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/Analytics">
                                            <FaChartLine style={{ marginRight: "8px" }} />Analytics
                                        </Link>
                                    </li>
                                    <li className='nav-item'>
                                        <Link className='nav-link' to="/Profile">
                                            <FaFileExport style={{ marginRight: "8px" }} />Profile
                                        </Link>
                                    </li>
                                    <li className='nav-item'>
                                        <button
                                            className='nav-link btn'
                                            onClick={logout}
                                        >
                                            <FaSignOutAlt style={{ marginRight: "8px" }} />Logout
                                        </button>
                                    </li>
                                </>
                            )}
                        </ul>
                    </div>
                </div>
            </nav>
            <Routes>
                <Route path="/" element={<About />} />
                <Route path="/signIn" element={<SignIn />} />
                <Route path="/signUp" element={<SignUp />} />
                <Route path="/Home" element={isAuthenticated ? <Home /> : <Navigate to="/" />} />
                <Route path="/Dashboard" element={isAuthenticated ? <Expenses /> : <Navigate to="/" />} />
                <Route path="/Analytics" element={isAuthenticated ? <Analytics /> : <Navigate to="/" />} />
                <Route path="/Profile" element={isAuthenticated ? <Profile /> : <Navigate to="/" />} />
                <Route path="/reset-password" element={<AccountPassword />} />
            </Routes>
        </>
    );
}

export default App;
