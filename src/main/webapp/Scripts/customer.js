let map = null;
let routeControl = null;
let pickupMarker = null;
let dropMarker = null;
let distanceKm = 0;
let durationTxt = "";

let debounceTimer = null;
let currentController = null;

let monitorInterval = null;
let currentRideId = localStorage.getItem("activeRideId");
let assignedDriverId = null;


const user = JSON.parse(localStorage.getItem("user"));
const token = localStorage.getItem("token");

if(!user || !token) {
    window.location.href="login.jsp";
} else if(user.role === "DRIVER") {
    alert("Logged in as Driver. Redirecting to Console...");
    window.location.href="driver.jsp";
} else {
    document.getElementById("userName").innerText = user.fullName;
}


map = L.map('map').setView([28.6139, 77.2090], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);


async function startRideMonitor() {
    if(monitorInterval) clearInterval(monitorInterval);

    monitorInterval = setInterval(async () => {
        if(!currentRideId) return;

        try {
            const res = await fetch("api/rides/status?id=" + currentRideId);

            if(res.status === 404 || res.status === 500) {
                console.warn("Ride not found (DB Reset detected). Clearing stale data.");
                localStorage.removeItem("activeRideId");
                clearInterval(monitorInterval);
                location.reload();
                return;
            }

            if(res.ok) {
                const ride = await res.json();

                if(ride.status === 'ACCEPTED' || ride.status === 'ONGOING') {
                    document.getElementById("statusAlert").classList.add("d-none");
                    document.getElementById("driverInfo").classList.remove("d-none");
                    document.getElementById("btnRoute").disabled = true;

                    if(ride.driver) {
                        assignedDriverId = ride.driver.id;
                        document.getElementById("dName").innerText = ride.driver.fullName;
                        document.getElementById("dPhone").href = "tel:" + ride.driver.phone;


                        if(ride.otp) {
                            document.getElementById("rideOtp").innerText = ride.otp;
                        }

                        if(ride.driver.averageRating > 0) {
                            document.getElementById("dRating").innerText = "⭐ " + ride.driver.averageRating;
                            document.getElementById("dRating").classList.remove("d-none");
                        } else {
                            document.getElementById("dRating").classList.add("d-none");
                        }

                        if(ride.driver.vehicle) {
                            const v = ride.driver.vehicle;
                            document.getElementById("dCar").innerText = v.make + " " + v.model + " (" + v.color + ")";
                            document.getElementById("dPlate").innerText = v.plateNumber;
                        } else {
                            document.getElementById("dCar").innerText = "Vehicle info loading...";
                        }
                    }

                    const statusText = document.getElementById("rideStatusText");
                    if (ride.status === 'ONGOING') {
                        statusText.className = "mt-3 alert alert-primary py-1 small text-center";
                        statusText.innerText = "Ride in Progress - To Destination 🚀";
                    } else {
                        statusText.className = "mt-3 alert alert-warning py-1 small text-center";
                        statusText.innerText = "Driver is arriving soon...";
                    }
                }
                else if(ride.status === 'COMPLETED') {
                    clearInterval(monitorInterval);
                    document.getElementById("driverInfo").classList.add("d-none");
                    showPaymentModal(ride.fare);
                }
            }
        } catch (err) { console.error("Monitor Error:", err); }
    }, 3000);
}

if(currentRideId) {
    document.getElementById("statusAlert").classList.remove("d-none");
    document.getElementById("btnRoute").disabled = true;
    startRideMonitor();
}


document.getElementById('pickup_input').addEventListener('input', (e) => handleInput(e.target.value, 'pickup-list'));
document.getElementById('drop_input').addEventListener('input', (e) => handleInput(e.target.value, 'drop-list'));

document.addEventListener('click', (e) => {
    if (!e.target.closest('.search-container')) {
        document.getElementById('pickup-list').classList.add('d-none');
        document.getElementById('drop-list').classList.add('d-none');
    }
});

function handleInput(query, listId) {
    clearTimeout(debounceTimer);
    if(query.length < 3) {
        document.getElementById(listId).classList.add('d-none');
        return;
    }
    debounceTimer = setTimeout(() => performSearch(query, listId), 400);
}

async function performSearch(query, listId) {
    if (currentController) currentController.abort();
    currentController = new AbortController();

    try {
        const list = document.getElementById(listId);
        const res = await fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + query + '&limit=5', { signal: currentController.signal });
        const data = await res.json();

        list.innerHTML = "";
        if (data.length > 0) {
            list.classList.remove("d-none");
            data.forEach(place => {
                const div = document.createElement("div");
                div.className = "search-item";
                const shortName = place.display_name.split(",").slice(0, 3).join(",");
                div.innerText = shortName;
                div.onclick = () => selectLocation(place, listId, shortName);
                list.appendChild(div);
            });
        } else {
            list.classList.add("d-none");
        }
    } catch (err) { if (err.name !== 'AbortError') console.error(err); }
}

function selectLocation(place, listId, displayName) {
    if (currentController) currentController.abort();
    clearTimeout(debounceTimer);

    const isPickup = listId === 'pickup-list';
    document.getElementById(isPickup ? 'pickup_input' : 'drop_input').value = displayName;
    document.getElementById(listId).classList.add("d-none");

    if (isPickup) {
        document.getElementById("p_lat").value = place.lat;
        document.getElementById("p_lng").value = place.lon;
        if(pickupMarker) map.removeLayer(pickupMarker);
        pickupMarker = L.marker([place.lat, place.lon]).addTo(map).bindPopup("Pickup").openPopup();
        map.setView([place.lat, place.lon], 14);
    } else {
        document.getElementById("d_lat").value = place.lat;
        document.getElementById("d_lng").value = place.lon;
        if(dropMarker) map.removeLayer(dropMarker);
        dropMarker = L.marker([place.lat, place.lon]).addTo(map).bindPopup("Drop");
    }
}

function calculateRoute() {
    const pLat = document.getElementById("p_lat").value;
    const dLat = document.getElementById("d_lat").value;

    if(!pLat || !dLat) { alert("Please select both locations!"); return; }
    if(routeControl) map.removeControl(routeControl);

    routeControl = L.Routing.control({
        waypoints: [
            L.latLng(document.getElementById("p_lat").value, document.getElementById("p_lng").value),
            L.latLng(document.getElementById("d_lat").value, document.getElementById("d_lng").value)
        ],
        routeWhileDragging: false, show: false, createMarker: () => null
    }).on('routesfound', function(e) {
        const route = e.routes[0];
        distanceKm = (route.summary.totalDistance / 1000).toFixed(2);
        durationTxt = Math.round(route.summary.totalTime / 60) + " mins";

        document.getElementById("dist-display").innerText = distanceKm;
        document.getElementById("price-auto").innerText = "₹" + Math.round(distanceKm * 10);
        document.getElementById("price-mini").innerText = "₹" + Math.round(distanceKm * 15);
        document.getElementById("price-prime").innerText = "₹" + Math.round(distanceKm * 20);

        document.getElementById("fare-options").classList.remove("d-none");
    }).addTo(map);
}

async function selectRide(type, rate) {
    if(!user) return;
    const fare = Math.round(distanceKm * rate);
    if(!confirm("Confirm " + type + " ride for ₹" + fare + "?")) return;

    const data = {
        customerId: user.id,
        pickup: document.getElementById("pickup_input").value,
        drop: document.getElementById("drop_input").value,
        pickupLat: document.getElementById("p_lat").value,
        pickupLng: document.getElementById("p_lng").value,
        dropLat: document.getElementById("d_lat").value,
        dropLng: document.getElementById("d_lng").value,
        distance: distanceKm,
        fare: fare,
        duration: durationTxt
    };

    const res = await fetch("api/rides/book", {
        method: "POST", headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
    });

    if(res.ok) {
        const ride = await res.json();
        alert("Booking Confirmed! Waiting for driver...");

        localStorage.setItem("activeRideId", ride.id);
        currentRideId = ride.id;

        document.getElementById("statusAlert").classList.remove("d-none");
        document.getElementById("fare-options").classList.add("d-none");
        document.getElementById("btnRoute").disabled = true;

        startRideMonitor();
    } else {
        alert("Booking Failed.");
    }
}

function showPaymentModal(fare) {

    document.getElementById("originalFare").textContent = fare;
    document.getElementById("finalAmount").textContent = fare;
    document.getElementById("couponDiscountAmount").textContent = "0";


    document.getElementById("couponSelect").value = "none";


    const completedRides = user.completedRides || 0;

    if (completedRides === 0 && !user.usedNewRider) {
        document.getElementById("newriderOption").style.display = "block";
    } else {
        document.getElementById("newriderOption").style.display = "none";
    }

    if (completedRides >= 10 && !user.usedTenRides) {
        document.getElementById("10ridesOption").style.display = "block";
    } else {
        document.getElementById("10ridesOption").style.display = "none";
    }

    if (completedRides >= 50 && !user.usedFiftyRides) {
        document.getElementById("50ridesOption").style.display = "block";
    } else {
        document.getElementById("50ridesOption").style.display = "none";
    }

    document.getElementById("paymentModal").classList.remove("d-none");
}

function applyCoupon() {
    const selectedCoupon = document.getElementById("couponSelect").value;
    const originalFare = parseFloat(document.getElementById("originalFare").textContent);

    let discount = 0;

    switch(selectedCoupon) {
        case "newrider":
            discount = 50;
            break;
        case "10rides":
            discount = 50;
            break;
        case "50rides":
            discount = 70;
            break;
        default:
            discount = 0;
    }

    document.getElementById("couponDiscountAmount").textContent = discount;
    document.getElementById("finalAmount").textContent = Math.max(0, originalFare - discount);
}

async function submitPayment() {
    const selectedCoupon = document.getElementById("couponSelect").value;
    const finalAmount = parseFloat(document.getElementById("finalAmount").textContent);

    const data = {
        rideId: currentRideId,
        amount: finalAmount,
        method: document.getElementById("payMethod").value,
        coupon: selectedCoupon
    };

    const response = await fetch("api/payments", {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        const result = await response.json();
        alert("Payment Successful!");


        user.loyaltyPoints = result.loyaltyPoints;
        localStorage.setItem("user", JSON.stringify(user));

        document.getElementById("paymentModal").classList.add("d-none");
        document.getElementById("ratingModal").classList.remove("d-none");
    } else {
        alert("Payment Failed. Please try again.");
    }
}

async function submitRating() {
    const data = {
        rideId: currentRideId,
        givenBy: user.id,
        givenTo: assignedDriverId,
        score: parseInt(document.getElementById("rateScore").value),
        comment: document.getElementById("rateComment").value
    };

    await fetch("api/ratings", { method: "POST", body: JSON.stringify(data) });

    alert("Thank you! Rating submitted.");
    localStorage.removeItem("activeRideId");
    location.reload();
}

function logout() {
    localStorage.clear();
    window.location.href = "login.jsp";
}