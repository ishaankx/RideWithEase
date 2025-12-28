// 1. Initialize Page
const storedRole = localStorage.getItem("selectedRole");

if(storedRole === 'DRIVER') {
    document.getElementById("regRole").value = "DRIVER";
} else {
    document.getElementById("regRole").value = "RIDER";
}

document.getElementById("formTitle").innerText = (storedRole === "DRIVER" ? "Driver" : "Rider") + " Login";
toggleDriverFields();

function toggleForm() {
    document.getElementById("loginForm").classList.toggle("d-none");
    document.getElementById("signupForm").classList.toggle("d-none");
    const isLogin = !document.getElementById("loginForm").classList.contains("d-none");
    document.getElementById("formTitle").innerText = isLogin ? "Login" : "Sign Up";
}

function toggleDriverFields() {
    const role = document.getElementById("regRole").value;
    const fields = document.getElementById("driverFields");
    if(role === 'DRIVER') {
        fields.classList.remove("d-none");
    } else {
        fields.classList.add("d-none");
    }
}

async function register() {
    const roleVal = document.getElementById("regRole").value;
    const data = {
        fullName: document.getElementById("regName").value,
        email: document.getElementById("regEmail").value,
        phone: document.getElementById("regPhone").value,
        password: document.getElementById("regPass").value,
        role: roleVal
    };

    if (roleVal === 'DRIVER') {
        data.license = document.getElementById("regLicense").value;
        data.plate = document.getElementById("regPlate").value;
        data.make = document.getElementById("regMake").value;
        data.model = document.getElementById("regModel").value;
        data.color = document.getElementById("regColor").value;
        data.year = parseInt(document.getElementById("regYear").value);
    }

    try {
        const res = await fetch("api/auth/register", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if(res.ok) {
            alert("Registration Successful! Please Login.");
            toggleForm();
        } else {
            alert("Registration failed. Check inputs.");
        }
    } catch (err) {
        console.error(err);
        alert("Server Error");
    }
}

async function login() {
    const data = {
        email: document.getElementById("loginKey").value,
        password: document.getElementById("loginPass").value
    };

    try {
        const res = await fetch("api/auth/login", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if(res.ok) {
            const responseData = await res.json();

            localStorage.setItem("token", responseData.token);
            localStorage.setItem("user", JSON.stringify(responseData.user));

            const userRole = responseData.role;

            // --- REDIRECTS TO JSP ---
            if (userRole === "DRIVER") {
                window.location.href = "driver.jsp";
            } else {
                window.location.href = "customer.jsp";
            }
        } else {
            document.getElementById("alertBox").classList.remove("d-none");
            document.getElementById("alertBox").innerText = "Invalid Credentials";
        }
    } catch (err) {
        console.error(err);
        document.getElementById("alertBox").classList.remove("d-none");
        document.getElementById("alertBox").innerText = "Connection Error";
    }
}