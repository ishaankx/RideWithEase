const driver = JSON.parse(localStorage.getItem("user"));
if(driver) document.getElementById("driverName").innerText = "Welcome, " + driver.fullName;

const modal = new bootstrap.Modal(document.getElementById('rideModal'));


let availableRides = [];
let selectedRide = null;
let currentRideId = null;
let map = null;


async function loadRides() {
    if(currentRideId) return;

    try {
        const res = await fetch("api/rides/available");
        const rides = await res.json();

        availableRides = rides;
        const list = document.getElementById("rides-list");
        list.innerHTML = "";

        if(rides.length === 0) {
            document.getElementById("no-rides-msg").classList.remove("d-none");
        } else {
            document.getElementById("no-rides-msg").classList.add("d-none");
        }

        rides.forEach(r => {
            const ratingHtml = r.customer.averageRating > 0
                ? '<span class="badge bg-warning text-dark">⭐ ' + r.customer.averageRating + '</span>'
                : '<span class="badge bg-secondary">New User</span>';

            list.innerHTML +=
                '<div class="col-md-6 mb-3">' +
                '<div class="card p-3 shadow-sm border-0">' +
                '<div class="d-flex justify-content-between align-items-center">' +
                '<div style="width: 60%">' +
                '<h6 class="mb-1 text-truncate" title="' + r.pickupLoc + '">📍 ' + r.pickupLoc + '</h6>' +
                '<small class="text-muted">to</small>' +
                '<h6 class="mb-1 text-truncate" title="' + r.dropLoc + '">🏁 ' + r.dropLoc + '</h6>' +
                '<div class="mt-2 text-muted small">Pass: ' + r.customer.fullName + ' ' + ratingHtml + '</div>' +
                '</div>' +
                '<div class="text-end">' +
                '<h4 class="text-success mb-0">₹' + r.fare + '</h4>' +
                '<small>' + r.distanceKm + ' km</small>' +
                '<button class="btn btn-dark btn-sm d-block mt-2 w-100" onclick="openModal(' + r.id + ')">View</button>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>';
        });
    } catch(e) { console.error(e); }
}


function openModal(rideId) {
    const r = availableRides.find(ride => ride.id === rideId);
    if(!r) return;

    selectedRide = r;

    const ratingStr = r.customer.averageRating > 0 ? ' (⭐ ' + r.customer.averageRating + ')' : "";
    document.getElementById("mUser").innerText = r.customer.fullName + ratingStr;
    document.getElementById("mPick").innerText = r.pickupLoc;
    document.getElementById("mDrop").innerText = r.dropLoc;
    document.getElementById("mFare").innerText = r.fare;

    modal.show();

    setTimeout(() => {
        if(map) { map.remove(); }
        map = L.map('driverMap').setView([r.pickupLat, r.pickupLng], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

        L.Routing.control({
            waypoints: [
                L.latLng(r.pickupLat, r.pickupLng),
                L.latLng(r.dropLat, r.dropLng)
            ],
            show: false,
            draggableWaypoints: false,
            addWaypoints: false
        }).addTo(map);
    }, 500);
}


async function acceptRide() {
    if(!driver) { alert("Not logged in!"); window.location.href="login.jsp"; return; }

    const data = { rideId: selectedRide.id, driverId: driver.id };
    const res = await fetch("api/rides/accept", { method: "POST", body: JSON.stringify(data) });

    if(res.ok) {
        modal.hide();
        currentRideId = selectedRide.id;
        activateRidePanel(selectedRide);
    } else {
        alert("Failed to accept.");
        loadRides();
    }
}

function activateRidePanel(ride) {
    document.getElementById("availableRidesSection").classList.add("d-none");
    document.getElementById("activeRidePanel").classList.remove("d-none");

    document.getElementById("arUser").innerText = ride.customer.fullName;
    document.getElementById("arPick").innerText = ride.pickupLoc;
    document.getElementById("arDrop").innerText = ride.dropLoc;
    document.getElementById("arFare").innerText = ride.fare;

    document.getElementById("btnStart").classList.remove("d-none");
    document.getElementById("btnComplete").classList.add("d-none");
}



function openOtpModal() {
    document.getElementById('otpModal').classList.remove('d-none');
    document.getElementById('otpInput').value = '';
    document.getElementById('otpInput').focus();
}

async function submitOtpAndStart() {
    const enteredOtp = document.getElementById("otpInput").value;
    if (enteredOtp.length !== 4) {
        alert("Please enter a valid 4-digit PIN.");
        return;
    }


    const data = {
        rideId: currentRideId,
        status: 'ONGOING',
        otp: enteredOtp
    };

    const res = await fetch("api/rides/updateStatus", { method: "POST", body: JSON.stringify(data) });

    if (res.ok) {

        document.getElementById("otpModal").classList.add("d-none");
        document.getElementById("btnStart").classList.add("d-none");
        document.getElementById("btnComplete").classList.remove("d-none");
        alert("OTP Verified! Trip Started.");
    } else {
        alert("Incorrect PIN. Please try again.");
    }
}

async function updateStatus(status) {
    const data = { rideId: currentRideId, status: status };
    await fetch("api/rides/updateStatus", { method: "POST", body: JSON.stringify(data) });

    if (status === 'ONGOING') {

        document.getElementById("btnStart").classList.add("d-none");
        document.getElementById("btnComplete").classList.remove("d-none");
    }
    else if (status === 'COMPLETED') {
        document.getElementById("ratingModal").classList.remove("d-none");
    }
}

async function submitRating() {
    const data = {
        rideId: currentRideId,
        givenBy: driver.id,
        givenTo: selectedRide.customer.id,
        score: parseInt(document.getElementById("rateScore").value),
        comment: document.getElementById("rateComment").value
    };

    const res = await fetch("api/ratings", { method: "POST", body: JSON.stringify(data) });

    if(res.ok) {
        alert("Trip Finished & Rating Submitted!");

        document.getElementById("ratingModal").classList.add("d-none");
        currentRideId = null;
        document.getElementById("activeRidePanel").classList.add("d-none");
        document.getElementById("availableRidesSection").classList.remove("d-none");
        loadRides();
    } else {
        alert("Error submitting rating.");
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "login.jsp";
}

loadRides();