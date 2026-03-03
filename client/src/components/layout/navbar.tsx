import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

export default function NavBar(){
    const { user, logout } = useContext(AuthContext);
    
    return (
        <nav className="w-full flex items-center justify-between bg-background font-sans py-5 px-10  border border-red-500">
                <div className="flex gap-2 text-lg">
                    <span>🖥️</span>

                    <div className="flex items-center gap-2">

                        <h1 className="font-medium">Desk Nova</h1>
                        <div className="text-xs border rounded-sm px-2 py-1 text-muted-foreground">{user?.role ?? "Error"}</div>
                    </div>
                </div>
                <button onClick={logout} className="hover:bg-secondary/55 hover:cursor-pointer text-sm font-extralight  text-foreground border px-4 py-2 rounded-sm">
                        Logout
                </button>
        </nav>
    );
}
