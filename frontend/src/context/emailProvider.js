import React, { useState, useEffect } from "react";
import EmailContext from "./emailContext";

export default function EmailProvider({ children }) {
  const [email, setEmail] = useState(() => localStorage.getItem("email") || "");

  useEffect(() => {
    localStorage.setItem("email", email);
  }, [email]);

  return (
    <EmailContext.Provider value={{ email, setEmail }}>
      {children}
    </EmailContext.Provider>
  );
}