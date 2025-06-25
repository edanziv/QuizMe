import React, { useState } from "react";
import UserNameContext from "./UserNameContext";

export default function UserNameProvider({ children }) {
    const [username, setUsername] = useState("");

    return (
        <UserNameContext.Provider value={{ username, setUsername }}>
            {children}
        </UserNameContext.Provider>
    );
}