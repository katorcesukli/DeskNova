const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

document.addEventListener("DOMContentLoaded", () => {

    const loginBtn = document.getElementById("loginBtn");
    const errorEl = document.getElementById("error");

    // Check if user is already logged in
    checkSession();

    loginBtn.addEventListener("click", login);


    function login() {
        const firstName = document.getElementById("firstName").value.trim();
        const lastName = document.getElementById("lastName").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        errorEl.innerText = "";
        loginBtn.disabled = true;

        if (!firstName) {
            errorEl.innerText = "Please enter your first name.";
            loginBtn.disabled = false;
            return;
        }
        if (!lastName) {
            errorEl.innerText = "Please enter your last name.";
            loginBtn.disabled = false;
            return;
        }
        if (!email) {
            errorEl.innerText = "Please enter your email.";
            loginBtn.disabled = false;
            return;
        }
        if (!password) {
            errorEl.innerText = "Please enter your password.";
            loginBtn.disabled = false;
            return;
        }

        // Optionally, you might want to validate email format
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            errorEl.innerText = "Please enter a valid email address.";
            loginBtn.disabled = false;
            return;
        }

        fetch(`${BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ firstName, lastName, email, password })
        })
            .then(async response => {
                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || data.error || "Login failed");
                }

                return data;
            })

            .then(data => {

                console.log("FULL LOGIN RESPONSE:", data);

                if (!data.token) {
                    throw new Error("No token received from server");
                }

                // Save JWT token
                localStorage.setItem(TOKEN_KEY, data.token);

                // Save full user session
                const sessionUser = {
                    firstName: data.firstName,
                    lastName: data.lastName,
                    email: data.email,
                    role: data.role,
                    id: data.id || data.id  //AUTO GENERATED ID FOR NOW(WAITING FOR CONFIRMATION WITH FORMATTED ID)
                };

                localStorage.setItem(USER_KEY, JSON.stringify(sessionUser));

                console.log("Login successful");
                console.log("Role:", sessionUser.role);
                console.log("ID:", sessionUser.id);

                redirectByRole(sessionUser.role);
            })
            .catch(error => {
                console.error("Login Error:", error);

                errorEl.innerText =
                    error.message ||
                    "Invalid input.";
            })
            .finally(() => {
                loginBtn.disabled = false;
            });
    }


    function redirectByRole(role) {

        if (!role) {
            window.location.href = "login.html";
            alert("Invalid, redirecting to login page")
            return;
        }


        if (role.toUpperCase() === "ADMIN") {
            window.location.href = "admin.html";

        }
        if (role.toUpperCase() === "CLIENT") {
            window.location.href = "client.html";
        }
        if (role.toUpperCase() === "AGENT") {
            window.location.href = "agent.html";
        }

    }

    function checkSession() {

        const token = localStorage.getItem(TOKEN_KEY);
        const savedUser = localStorage.getItem(USER_KEY);

        if (token && savedUser) {

            const user = JSON.parse(savedUser);
            redirectByRole(user.role);

        }

    }


});