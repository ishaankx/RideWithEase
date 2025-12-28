<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Driver Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css" />
    <link rel="stylesheet" href="Styles/driver.css">
</head>
<body class="bg-dark text-white p-4">

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>🚖 Driver Console</h2>
        <div class="text-end">
            <span id="driverName" class="me-3 text-warning"></span>
            <button onclick="logout()" class="btn btn-outline-light btn-sm">Logout</button>
        </div>
    </div>

    <div id="activeRidePanel" class="card p-4 mb-4 active-ride-card text-dark d-none">
        <h4 class="text-success border-bottom pb-2">🟢 Current Ride</h4>
        <div class="row">
            <div class="col-md-8">
                <h5 id="arUser">Passenger Name</h5>
                <p class="mb-1"><strong>Pickup:</strong> <span id="arPick"></span></p>
                <p class="mb-1"><strong>Drop:</strong> <span id="arDrop"></span></p>
                <p class="mb-3"><strong>Fare:</strong> ₹<span id="arFare"></span></p>
            </div>
            <div class="col-md-4 d-flex align-items-center justify-content-center">
                <div id="rideControls" class="w-100">
                    <button id="btnStart" onclick="updateStatus('ONGOING')" class="btn btn-primary w-100 py-3 mb-2 fw-bold">🚀 Start Trip</button>
                    <button id="btnComplete" onclick="updateStatus('COMPLETED')" class="btn btn-success w-100 py-3 fw-bold d-none">✅ Complete Trip</button>
                </div>
            </div>
        </div>
    </div>

    <div id="availableRidesSection">
        <div class="d-flex justify-content-between align-items-center mb-2">
            <h4>Available Requests</h4>
            <button onclick="loadRides()" class="btn btn-warning btn-sm">🔄 Refresh</button>
        </div>
        <div id="rides-list" class="row"></div>
        <div id="no-rides-msg" class="alert alert-secondary d-none">No new ride requests currently.</div>
    </div>
</div>

<div class="modal fade text-dark" id="rideModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header"><h5>New Ride Request</h5></div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-5">
                        <p><strong>Customer:</strong> <span id="mUser"></span></p>
                        <p><strong>From:</strong> <span id="mPick"></span></p>
                        <p><strong>To:</strong> <span id="mDrop"></span></p>
                        <h4 class="text-success mt-3">Earnings: ₹<span id="mFare"></span></h4>
                    </div>
                    <div class="col-md-7">
                        <div id="driverMap"></div>
                    </div>
                </div>
                <button onclick="acceptRide()" class="btn btn-success w-100 mt-3 p-2 fw-bold">ACCEPT RIDE</button>
            </div>
        </div>
    </div>
</div>

<div id="ratingModal" class="modal-overlay d-none">
    <div class="card p-4 shadow-lg text-dark" style="width: 350px;">
        <h4 class="text-center">Rate Passenger</h4>
        <p class="text-center text-muted">How was the ride?</p>
        <select id="rateScore" class="form-select mb-3">
            <option value="5">⭐⭐⭐⭐⭐ (Excellent)</option>
            <option value="4">⭐⭐⭐⭐ (Good)</option>
            <option value="3">⭐⭐⭐ (Average)</option>
            <option value="2">⭐⭐ (Poor)</option>
            <option value="1">⭐ (Terrible)</option>
        </select>
        <textarea id="rateComment" class="form-control mb-3" placeholder="Any comments?"></textarea>
        <button onclick="submitRating()" class="btn btn-primary w-100">Submit Rating</button>
    </div>
</div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="Scripts/driver.js"></script>
</body>
</html>