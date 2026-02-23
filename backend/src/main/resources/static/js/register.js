const BASE_URL = "http://localhost:8080/api";

function register() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorEl = document.getElementById("error");

    errorEl.innerText = "";
    // registerBtn.disabled = true;

    // Validation
    if (firstName.length == null) {
        errorEl.innerText = "First name is required.";
        return;
    }
    if (lastName.length == null) {
        errorEl.innerText = "Last name is required.";
        return;
    }
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
        errorEl.innerText = "Please enter a valid email address.";
        // registerBtn.disabled = false;
        return;
    }

    if (password.length < 4) {
        errorEl.innerText = "Password must be at least 4 characters.";
        return;
    }

    const payload = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password,
        role: "CLIENT"
    };

    fetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then(response => {

            if (!response.ok) {
                return response.json().then(err => {
                    throw err;
                });
            }

            return response.json();
        })
        .then(data => {
            alert("Registration successful!");
            window.location.href = "login.html";
        })
        .catch(error => {

            if (error.message) {
                errorEl.innerText = error.message;
            } else {
                errorEl.innerText = "Registration failed.";
            }

            console.error(error);
        });

}
