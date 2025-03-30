import React, { createContext, useState, useContext, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const decoded = jwtDecode(token);
                const currentTime = Date.now() / 1000;
                if (decoded.exp > currentTime) {
                    setIsAuthenticated(true);
                } else {
                    localStorage.removeItem("token");
                    setIsAuthenticated(false);
                }
            } catch (error) {
                console.error("Invalid token:", error);
                localStorage.removeItem("token");
                setIsAuthenticated(false);
            }
        }
    }, []);

    const login = (token) => {
        localStorage.setItem("token", token);
        setIsAuthenticated(true);
    };

    const logout = () => {
        localStorage.removeItem("token");
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);