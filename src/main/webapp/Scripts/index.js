function selectRole(role) {
    localStorage.setItem("selectedRole", role);
    // Redirect to JSP
    window.location.href = "login.jsp";
}