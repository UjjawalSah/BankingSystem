// responseHandler.js
function handleResponse(status, message) {
    if (status === "success") {
        Swal.fire({
            title: 'Success',
            text: message,
            icon: 'success'
        }).then(() => {
            window.location.href = 'index.html';
        });
    } else {
        Swal.fire({
            title: 'Error',
            text: message,
            icon: 'error'
        }).then(() =>{
			 window.location.href = 'index.html';
		}
			
		)
        
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const status = document.getElementById("status").value;
    const message = document.getElementById("message").value;
    handleResponse(status, message);
});
