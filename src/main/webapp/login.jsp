<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - RideWithEase</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light p-5">
<div class="container" style="max-width: 450px;">
    <div class="card p-4 shadow-sm">
        <h3 id="formTitle" class="text-center mb-3">Login</h3>
        <div id="alertBox" class="alert alert-danger d-none"></div>

        <div id="loginForm">
            <input type="text" id="loginKey" class="form-control mb-3" placeholder="Email or Phone">
            <input type="password" id="loginPass" class="form-control mb-3" placeholder="Password">
            <button onclick="login()" class="btn btn-primary w-100">Login</button>
            <p class="mt-3 text-center">New here? <a href="#" onclick="toggleForm()">Create an Account</a></p>
        </div>

        <div id="signupForm" class="d-none">
            <input type="text" id="regName" class="form-control mb-2" placeholder="Full Name">
            <input type="text" id="regEmail" class="form-control mb-2" placeholder="Email">
            <input type="text" id="regPhone" class="form-control mb-2" placeholder="Phone Number">
            <input type="password" id="regPass" class="form-control mb-2" placeholder="Password">

            <label class="form-label mt-2 small text-muted">I want to join as:</label>
            <select id="regRole" class="form-select mb-2" onchange="toggleDriverFields()">
                <option value="RIDER">Rider (Passenger)</option>
                <option value="DRIVER">Driver (Captain)</option>
            </select>

            <div id="driverFields" class="d-none bg-light p-2 border rounded mb-2">
                <h6 class="text-muted small">Vehicle Details</h6>
                <input type="text" id="regLicense" class="form-control mb-2" placeholder="Driving License Number">
                <input type="text" id="regPlate" class="form-control mb-2" placeholder="Vehicle Plate Number">
                <input type="text" id="regMake" class="form-control mb-2" placeholder="Car Make (e.g. Toyota)">
                <input type="text" id="regModel" class="form-control mb-2" placeholder="Car Model (e.g. Corolla)">
                <input type="text" id="regColor" class="form-control mb-2" placeholder="Car Color (e.g. White)">
                <input type="number" id="regYear" class="form-control mb-2" placeholder="Manufacturing Year">
            </div>

            <button onclick="register()" class="btn btn-success w-100 mt-2">Sign Up</button>
            <p class="mt-3 text-center">Have an account? <a href="#" onclick="toggleForm()">Login</a></p>
        </div>
    </div>
</div>
<script src="Scripts/login.js"></script>
</body>
</html>