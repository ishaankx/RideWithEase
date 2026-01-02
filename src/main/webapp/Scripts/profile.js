const token = localStorage.getItem("token");

if(!token) {
    window.location.href="login.jsp";
}

function logout() {
    localStorage.clear();
    window.location.href = "login.jsp";
}

async function loadProfile() {
    try {
        const response = await fetch("api/profile", {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!response.ok) {

            console.error("Profile load failed: " + response.status);
            alert("Session expired. Please login again.");
            logout();
            return;
        }

        const data = await response.json();


        document.getElementById("profileName").innerText = data.fullName;
        document.getElementById("profileEmail").innerText = data.email;
        document.getElementById("loyaltyPoints").innerText = data.loyaltyPoints;
        document.getElementById("ridesCount").innerText = data.completedRides;


        const couponList = document.getElementById("couponList");
        couponList.innerHTML = "";

        let hasCoupons = false;


        if (data.completedRides === 0 && !data.usedNewRider) {
            addCoupon(couponList, "🎉 NEWRIDER", "₹50 OFF on your first ride!");
            hasCoupons = true;
        }
        if (data.completedRides >= 10 && !data.usedTenRides) {
            addCoupon(couponList, "🚀 10RIDES", "₹50 OFF for completing 10 rides!");
            hasCoupons = true;
        }
        if (data.completedRides >= 50 && !data.usedFiftyRides) {
            addCoupon(couponList, "🏆 50RIDES", "₹70 OFF for completing 50 rides!");
            hasCoupons = true;
        }

        if (!hasCoupons) {
            couponList.innerHTML = '<li class="list-group-item text-muted text-center py-3">No coupons available right now.</li>';
        }


        const historyList = document.getElementById("rideHistory");
        historyList.innerHTML = "";

        if (!data.rides || data.rides.length === 0) {
            historyList.innerHTML = '<div class="text-center py-5 text-muted">No ride history found.</div>';
        } else {
            data.rides.forEach(ride => {
                const item = document.createElement("div");
                item.className = "list-group-item p-3 border-bottom";


                let badgeClass = "bg-secondary";
                if(ride.status === 'COMPLETED') badgeClass = "bg-success";
                if(ride.status === 'CANCELLED') badgeClass = "bg-danger";
                if(ride.status === 'ONGOING') badgeClass = "bg-primary";

                item.innerHTML =
                    '<div class="d-flex justify-content-between align-items-center">' +
                    '<div>' +
                    '<h6 class="mb-1 text-primary">' + ride.dropLoc + '</h6>' +
                    '<small class="text-muted"><i class="fa-solid fa-calendar"></i> ' + ride.date + '</small>' +
                    '<div class="small mt-1 text-secondary">From: ' + ride.pickupLoc + '</div>' +
                    '</div>' +
                    '<div class="text-end">' +
                    '<span class="fw-bold fs-5">₹' + ride.fare + '</span><br>' +
                    '<span class="badge ' + badgeClass + '">' + ride.status + '</span>' +
                    '</div>' +
                    '</div>';
                historyList.appendChild(item);
            });
        }

    } catch (error) {
        console.error("Error loading profile:", error);
    }
}

function addCoupon(list, code, desc) {
    const li = document.createElement("li");
    li.className = "list-group-item d-flex justify-content-between align-items-center";
    li.innerHTML =
        '<div>' +
        '<h6 class="mb-0 fw-bold text-success">' + code + '</h6>' +
        '<small class="text-muted">' + desc + '</small>' +
        '</div>' +
        '<i class="fa-solid fa-tag text-success"></i>';
    list.appendChild(li);
}


loadProfile();