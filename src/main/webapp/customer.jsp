<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book a Ride - RideEase</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css" />
    <link rel="stylesheet" href="Styles/customer.css">
</head>
<body class="bg-light">

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="text-primary fw-bold">RideWithEase</h4>
        <div>
            <a href="profile.jsp" class="me-3 fw-bold text-dark text-decoration-none" id="userName"></a>
            <span id="loyaltyPoints" class="me-3 text-success fw-semibold"></span>
            <button onclick="logout()" class="btn btn-outline-danger btn-sm">Logout</button>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="card p-4 shadow-sm border-0">
                <h5 class="mb-3">Plan your journey</h5>

                <div class="mb-3 search-container">
                    <label class="form-label text-muted small fw-bold">PICKUP</label>
                    <input type="text" id="pickup_input" class="form-control" placeholder="Search Pickup Location..." autocomplete="off">
                    <div id="pickup-list" class="search-results d-none"></div>
                </div>

                <div class="mb-3 search-container">
                    <label class="form-label text-muted small fw-bold">DROP</label>
                    <input type="text" id="drop_input" class="form-control" placeholder="Search Drop Location..." autocomplete="off">
                    <div id="drop-list" class="search-results d-none"></div>
                </div>

                <input type="hidden" id="p_lat"><input type="hidden" id="p_lng">
                <input type="hidden" id="d_lat"><input type="hidden" id="d_lng">

                <button id="btnRoute" onclick="calculateRoute()" class="btn btn-dark w-100 py-2 fw-bold">See Route & Fares</button>

                <div id="statusAlert" class="alert alert-info mt-3 d-none">
                    Looking for drivers... <div class="spinner-border spinner-border-sm float-end"></div>
                </div>

                <div id="driverInfo" class="card mt-3 border-success d-none" style="background: #f0fff4;">
                    <div class="card-body">
                        <h6 class="text-success fw-bold">🚖 Driver Assigned!</h6>

                        <div class="alert alert-warning text-center fw-bold fs-4 py-2 mt-2">
                            PIN: <span id="rideOtp">----</span>
                        </div>

                        <hr>
                        <h4 id="dName" class="fw-bold">...</h4>
                        <div id="dRating" class="badge bg-warning text-dark mb-2 d-none"></div>

                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <div>
                                <div id="dCar" class="fw-bold">Loading Vehicle...</div>
                                <div id="dPlate" class="badge bg-dark">...</div>
                            </div>
                            <div class="text-end">
                                <a id="dPhone" href="#" class="btn btn-sm btn-outline-success">📞 Call</a>
                            </div>
                        </div>
                        <div class="mt-3 alert alert-warning py-1 small text-center" id="rideStatusText">
                            Arriving soon...
                        </div>
                    </div>
                </div>

                <div id="fare-options" class="mt-3 d-none">
                    <hr>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Total Distance:</span>
                        <span class="fw-bold"><span id="dist-display">0</span> km</span>
                    </div>
                    <div class="list-group">
                        <button class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" onclick="selectRide('AUTO', 10)">
                            <span>🛺 Auto</span> <span class="badge bg-primary rounded-pill" id="price-auto">₹0</span>
                        </button>
                        <button class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" onclick="selectRide('MINI', 15)">
                            <span>🚗 Mini</span> <span class="badge bg-primary rounded-pill" id="price-mini">₹0</span>
                        </button>
                        <button class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" onclick="selectRide('PRIME', 20)">
                            <span>🚙 SUV Prime</span> <span class="badge bg-primary rounded-pill" id="price-prime">₹0</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div id="map" class="shadow-sm"></div>
        </div>
    </div>
</div>

<div id="paymentModal" class="modal-overlay d-none">
    <div class="card p-4 shadow-lg" style="width: 400px;">
        <div class="text-center mb-3">
            <h1 class="text-success">✅</h1>
            <h4>Trip Completed!</h4>
            <p class="text-muted">Apply coupon and pay the fare.</p>
        </div>

        <div class="bg-light p-3 rounded mb-3">
            <div class="d-flex justify-content-between mb-2">
                <span>Original Fare:</span>
                <span>₹<span id="originalFare">0</span></span>
            </div>
            <div class="d-flex justify-content-between mb-2">
                <span>Coupon Discount:</span>
                <span class="text-success">-₹<span id="couponDiscountAmount">0</span></span>
            </div>
            <hr class="my-2">
            <div class="d-flex justify-content-between">
                <span class="fw-bold">Total to Pay:</span>
                <span class="fw-bold fs-5 text-primary">₹<span id="finalAmount">0</span></span>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label fw-bold">🎁 Apply Coupon</label>
            <select id="couponSelect" class="form-select" onchange="applyCoupon()">
                <option value="none">No Coupon</option>
                <option value="newrider" id="newriderOption" style="display: none;">🎉 NEWRIDER: 50₹ off</option>
                <option value="10rides" id="10ridesOption" style="display: none;">🚀 10RIDES: 50₹ off</option>
                <option value="50rides" id="50ridesOption" style="display: none;">🏆 50RIDES: 70₹ off</option>
            </select>
        </div>

        <label class="form-label fw-bold">💳 Payment Method</label>
        <select id="payMethod" class="form-select mb-3">
            <option value="CASH">💵 Cash</option>
            <option value="UPI">📱 UPI / QR</option>
            <option value="CREDIT_CARD">💳 Credit/Debit Card</option>
        </select>
        <button onclick="submitPayment()" class="btn btn-success w-100 py-2 fw-bold">Pay Now</button>
    </div>
</div>

<div id="ratingModal" class="modal-overlay d-none">
    <div class="card p-4 shadow-lg" style="width: 350px;">
        <h4 class="text-center">Rate Driver</h4>
        <p class="text-center text-muted">How was your journey?</p>
        <select id="rateScore" class="form-select mb-3">
            <option value="5">⭐⭐⭐⭐⭐ (Excellent)</option>
            <option value="4">⭐⭐⭐⭐ (Good)</option>
            <option value="3">⭐⭐⭐ (Average)</option>
            <option value="2">⭐⭐ (Poor)</option>
            <option value="1">⭐ (Terrible)</option>
        </select>
        <textarea id="rateComment" class="form-control mb-3" placeholder="Additional comments..."></textarea>
        <button onclick="submitRating()" class="btn btn-primary w-100">Submit Rating</button>
    </div>
</div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js"></script>
<script src="Scripts/customer.js"></script>
</body>
</html>
