<%--
  Created by IntelliJ IDEA.
  User: Ishaan
  Date: 02-01-2026
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Profile - RideWithEase</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Styles/profile.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="bg-light">

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <a href="customer.jsp" class="btn btn-outline-secondary">
            <i class="fa-solid fa-arrow-left"></i> Back to Map
        </a>
        <h4 class="text-primary fw-bold">My Profile</h4>
        <button onclick="logout()" class="btn btn-outline-danger btn-sm">Logout</button>
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 mb-3 text-center p-4">
                <div class="mb-3">
                    <i class="fa-solid fa-user-circle fa-5x text-secondary"></i>
                </div>
                <h4 id="profileName">Loading...</h4>
                <p class="text-muted" id="profileEmail">...</p>
                <hr>
                <div class="d-flex justify-content-around">
                    <div>
                        <h3 class="text-success fw-bold" id="loyaltyPoints">0</h3>
                        <small class="text-muted">Loyalty Points</small>
                    </div>
                    <div>
                        <h3 class="text-primary fw-bold" id="ridesCount">0</h3>
                        <small class="text-muted">Rides Taken</small>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm border-0 mb-3">
                <div class="card-header bg-white fw-bold"><i class="fa-solid fa-ticket"></i> My Coupons</div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush" id="couponList">
                        <li class="list-group-item text-muted text-center py-3">Loading coupons...</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white fw-bold"><i class="fa-solid fa-history"></i> Ride History</div>
                <div class="card-body p-0">
                    <div class="list-group list-group-flush" id="rideHistory">
                        <div class="text-center py-5 text-muted">
                            <div class="spinner-border text-primary" role="status"></div>
                            <p class="mt-2">Loading history...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="Scripts/profile.js"></script>
</body>
</html>
