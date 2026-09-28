
// ================= LOGIN =================

const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const email =
        document.getElementById("email").value;

    const password =
        document.getElementById("password").value;

    const message =
        document.getElementById("message");

    try {

        const response = await fetch("/api/users/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {

            // Store login information
            localStorage.setItem("token", data.token);
            localStorage.setItem("userId", data.userId);
            localStorage.setItem("name", data.name);
            localStorage.setItem("role", data.role);

            message.innerHTML =
                "Login successful!";

            setTimeout(() => {

                // ================= ADMIN =================

                if (data.role === "ADMIN") {

                    window.location.href =
                        "/admin-dashboard.html";

                }

                // ================= LOAN OFFICER =================

                else if (data.role === "LOAN_OFFICER") {

                    window.location.href =
                        "/officer-dashboard.html";

                }

                // ================= CUSTOMER =================

                else if (data.role === "CUSTOMER") {

                    window.location.href =
                        "/dashboard.html";

                }

                // ================= UNKNOWN ROLE =================

                else {

                    message.innerHTML =
                        "Unknown user role.";

                }

            }, 500);

        } else {

            message.innerHTML =
                data.error || "Login failed";
        }

    } catch (error) {

        console.error(error);

        message.innerHTML =
            "Unable to connect to server.";
    }

});


// ================= SHOW REGISTER =================

const showRegister =
    document.getElementById("showRegister");

const showLogin =
    document.getElementById("showLogin");

const loginSection =
    document.getElementById("loginSection");

const registerSection =
    document.getElementById("registerSection");


showRegister.addEventListener("click", function (event) {

    event.preventDefault();

    loginSection.style.display = "none";

    registerSection.style.display = "block";

});


// ================= SHOW LOGIN =================

showLogin.addEventListener("click", function (event) {

    event.preventDefault();

    registerSection.style.display = "none";

    loginSection.style.display = "block";

});


// ================= REGISTER =================

const registerForm =
    document.getElementById("registerForm");

registerForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const name =
        document.getElementById("registerName").value;

    const email =
        document.getElementById("registerEmail").value;

    const phone =
        document.getElementById("registerPhone").value;

    const password =
        document.getElementById("registerPassword").value;

    const message =
        document.getElementById("message");

    try {

        const response = await fetch(
            "/api/users/register",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    name: name,
                    email: email,
                    phone: phone,
                    password: password
                })
            }
        );

        const data = await response.json();

        if (response.ok) {

            message.innerHTML =
                "Registration successful! Please login.";

            registerForm.reset();

            setTimeout(() => {

                registerSection.style.display = "none";
                loginSection.style.display = "block";

            }, 1000);

        } else {

            if (data.error) {

                message.innerHTML =
                    data.error;

            } else {

                message.innerHTML =
                    "Registration failed.";
            }
        }

    } catch (error) {

        console.error(error);

        message.innerHTML =
            "Unable to connect to server.";
    }

})
