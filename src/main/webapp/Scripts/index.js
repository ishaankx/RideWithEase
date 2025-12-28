function selectRole(role) {
    localStorage.setItem("selectedRole", role);

    window.location.href = "login.jsp";
}