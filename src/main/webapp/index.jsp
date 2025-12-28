<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>RideWithEase</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Styles/index.css">
</head>
<body>
<div class="container d-flex justify-content-center">
    <div class="card role-card bg-primary text-white" onclick="selectRole('CUSTOMER')">
        <h3>Book a Ride</h3>
        <p>For Passengers</p>
    </div>
    <div class="card role-card bg-dark text-white" onclick="selectRole('DRIVER')">
        <h3>Driver</h3>
        <p>For Captains</p>
    </div>
</div>
<script src="Scripts/index.js"></script>
</body>
</html>