const token = localStorage.getItem("token");

const userName = localStorage.getItem("name");

const role = localStorage.getItem("role");
// alert("Role = " + role);


// ===============================
// CHECK LOGIN & ROLE
// ===============================

if (!token || role !== "ADMIN") {

    alert("Access denied. Admin login required.");

    window.location.href = "/index.html";
}


// ===============================
// DISPLAY ADMIN NAME
// ===============================

const adminNameElement =
    document.getElementById("adminName");

const adminWelcomeNameElement =
    document.getElementById("adminWelcomeName");

if (adminNameElement) {
    adminNameElement.textContent = userName || "Admin";
}

if (adminWelcomeNameElement) {
    adminWelcomeNameElement.textContent = userName || "Admin";
}


// ===============================
// LOAD DASHBOARD
// ===============================

async function loadDashboard() {

    try {

        const response = await fetch(
            "/api/admin/dashboard",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );


        if (!response.ok) {

            if (response.status === 401) {

                alert("Session expired. Please login again.");

                logout();

                return;
            }

            if (response.status === 403) {

                alert("You are not authorized to access Admin Dashboard.");

                window.location.href = "/index.html";

                return;
            }

            throw new Error(
                "Failed to load dashboard"
            );
        }


        const data = await response.json();


        // ===============================
        // UPDATE STATISTICS
        // ===============================

        document.getElementById("totalCustomers")
            .textContent = data.totalCustomers;

        document.getElementById("totalLoanOfficers")
            .textContent = data.totalLoanOfficers;

        document.getElementById("totalLoans")
            .textContent = data.totalLoans;

        document.getElementById("appliedLoans")
            .textContent = data.appliedLoans;

        document.getElementById("approvedLoans")
            .textContent = data.approvedLoans;

        document.getElementById("rejectedLoans")
            .textContent = data.rejectedLoans;

        document.getElementById("activeLoans")
            .textContent = data.activeLoans;

        document.getElementById("closedLoans")
            .textContent = data.closedLoans;


    } catch (error) {

        console.error(
            "Dashboard error:",
            error
        );

        const message =
            document.getElementById("dashboardMessage");

        if (message) {

            message.innerHTML =
                `<p class="error-message">
                    Unable to load dashboard data.
                 </p>`;
        }
    }
}

async function loadCustomers() {

    const tableBody =
        document.getElementById("customersTableBody");

    tableBody.innerHTML = `
        <tr>
            <td colspan="5">Loading customers...</td>
        </tr>
    `;

    try {

        const response = await fetch(
            "/api/admin/customers",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                alert("You are not authorized to view customers.");
                return;
            }

            throw new Error("Failed to load customers");
        }

        const customers = await response.json();

        if (customers.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No customers found.
                    </td>
                </tr>
            `;

            return;
        }

        tableBody.innerHTML = "";

		customers.forEach(customer => {

		    const row = document.createElement("tr");

		    row.innerHTML = `
		        <td>${customer.userId}</td>
		        <td>${customer.name}</td>
		        <td>${customer.email}</td>
		        <td>${customer.phone || "-"}</td>
		        <td>${customer.role}</td>
		        <td>${customer.totalLoans}</td>
				<td>
				    <button
				        class="primary-btn"
				        onclick="viewCustomerLoans(${customer.userId})">
				        💰 View Loans
				    </button>

				    <button
				        class="primary-btn"
				        onclick="viewCustomerDocuments(${customer.userId})">
				        📄 View KYC
				    </button>
				</td>
		    `;

		    tableBody.appendChild(row);
		});

    } catch (error) {

        console.error("Customers error:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Unable to load customers.
                </td>
            </tr>
        `;
    }
}

async function viewCustomerLoans(customerId) {

    const modal = document.getElementById("customerLoansModal");
    const content = document.getElementById("customerLoansContent");

    // Open modal
    modal.style.display = "flex";

    content.innerHTML = "Loading customer loans...";

    try {

        const currentToken = localStorage.getItem("token");

        if (!currentToken) {
            alert("Session expired. Please login again.");
            logout();
            return;
        }

        const response = await fetch(
            `/api/admin/customers/${customerId}/loans`,
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + currentToken
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                content.innerHTML = `
                    <p class="error-message">
                        You are not authorized to view customer loans.
                    </p>
                `;
                return;
            }

            throw new Error("Failed to load customer loans");
        }

        const loans = await response.json();

        // No loans
        if (loans.length === 0) {

            content.innerHTML = `
                <div class="no-loans-message">
                    <p>📋 No loans found for this customer.</p>
                </div>
            `;

            return;
        }

        // Display loans
        content.innerHTML = "";

        loans.forEach(loan => {

            const loanCard = document.createElement("div");

            loanCard.className = "loan-details-card";

            loanCard.innerHTML = `
                
                <h3>Loan #${loan.loanId}</h3>

                <div class="loan-details-grid">

                    <div>
                        <strong>Customer ID:</strong>
                        ${loan.userId}
                    </div>

                    <div>
                        <strong>Customer Name:</strong>
                        ${loan.customerName || "-"}
                    </div>

                    <div>
                        <strong>Email:</strong>
                        ${loan.customerEmail || "-"}
                    </div>

                    <div>
                        <strong>Loan Type:</strong>
                        ${loan.loanType || "-"}
                    </div>

                    <div>
                        <strong>Amount:</strong>
                        ₹${loan.amount || 0}
                    </div>

                    <div>
                        <strong>Interest Rate:</strong>
                        ${loan.interestRate || 0}%
                    </div>

                    <div>
                        <strong>Tenure:</strong>
                        ${loan.tenureMonths || 0} months
                    </div>

                    <div>
                        <strong>EMI:</strong>
                        ₹${loan.emiAmount || 0}
                    </div>

                    <div>
                        <strong>Status:</strong>
                        ${loan.status || "-"}
                    </div>

                    <div>
                        <strong>Applied Date:</strong>
                        ${loan.appliedDate || "-"}
                    </div>

                    <div>
                        <strong>Approved Date:</strong>
                        ${loan.approvedDate || "-"}
                    </div>

                </div>
            `;

            content.appendChild(loanCard);
        });

    } catch (error) {

        console.error("Customer loans error:", error);

        content.innerHTML = `
            <p class="error-message">
                Unable to load customer loans.
            </p>
        `;
    }
}
async function viewCustomerDocuments(customerId) {

    const modal =
        document.getElementById("customerKycModal");

    const content =
        document.getElementById("customerKycContent");

    modal.style.display = "flex";

    content.innerHTML = "Loading KYC documents...";

    try {

        const currentToken =
            localStorage.getItem("token");

        if (!currentToken) {
            alert("Session expired. Please login again.");
            logout();
            return;
        }

        const response = await fetch(
            `/api/admin/customers/${customerId}/documents`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + currentToken
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                content.innerHTML = `
                    <p class="error-message">
                        You are not authorized to view KYC documents.
                    </p>
                `;
                return;
            }

            throw new Error(
                "Failed to load KYC documents"
            );
        }

        const documents =
            await response.json();

        console.log("KYC Documents:", documents);

        if (!Array.isArray(documents)) {
            throw new Error("Invalid KYC response");
        }

        if (documents.length === 0) {

            content.innerHTML = `
                <div class="no-loans-message">
                    <p>📄 No KYC documents found for this customer.</p>
                </div>
            `;

            return;
        }

        content.innerHTML = "";

        documents.forEach(doc => {

            const documentCard =
                document.createElement("div");

            documentCard.className =
                "loan-details-card";

            documentCard.innerHTML = `

                <h3>
                    📄 ${doc.documentType || "-"}
                </h3>

                <div class="loan-details-grid">

                    <div>
                        <strong>Document ID:</strong>
                        ${doc.documentId || "-"}
                    </div>

                    <div>
                        <strong>Customer ID:</strong>
                        ${doc.customerId || "-"}
                    </div>

                    <div>
                        <strong>Customer Name:</strong>
                        ${doc.customerName || "-"}
                    </div>

                    <div>
                        <strong>Customer Email:</strong>
                        ${doc.customerEmail || "-"}
                    </div>

                    <div>
                        <strong>File Name:</strong>
                        ${doc.fileName || "-"}
                    </div>

                    <div>
                        <strong>Status:</strong>
                        ${doc.verificationStatus || "-"}
                    </div>

                    <div>
                        <strong>Uploaded Date:</strong>
                        ${doc.uploadedDate || "-"}
                    </div>

                    <div>
                        <strong>Verified Date:</strong>
                        ${doc.verifiedDate || "-"}
                    </div>

                </div>
            `;

            content.appendChild(documentCard);

        });

    } catch (error) {

        console.error(
            "Customer KYC error:",
            error
        );

        content.innerHTML = `
            <p class="error-message">
                Unable to load KYC documents.
            </p>
        `;
    }
}


function closeCustomerKycModal() {

    const modal =
        document.getElementById("customerKycModal");

    modal.style.display = "none";
}


function closeCustomerKycModal() {

    const modal =
        document.getElementById("customerKycModal");

    modal.style.display = "none";
}
function closeCustomerLoansModal() {

    const modal =
        document.getElementById("customerLoansModal");

    modal.style.display = "none";
}

async function loadLoanOfficers() {

    const tableBody =
        document.getElementById("officersTableBody");

    tableBody.innerHTML = `
        <tr>
            <td colspan="5">Loading loan officers...</td>
        </tr>
    `;

    try {

        const response = await fetch(
            "/api/admin/loan-officers",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                alert("You are not authorized to view loan officers.");
                return;
            }

            throw new Error("Failed to load loan officers");
        }

        const officers = await response.json();

        if (officers.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No loan officers found.
                    </td>
                </tr>
            `;

            return;
        }

        tableBody.innerHTML = "";

        officers.forEach(officer => {

            const row = document.createElement("tr");

			row.innerHTML = `
			    <td>${officer.userId}</td>
			    <td>${officer.name || "-"}</td>
			    <td>${officer.email || "-"}</td>
			    <td>${officer.phone || "-"}</td>
			    <td>${officer.role || "-"}</td>

			    <td>
			        <button
			            class="primary-btn"
			            onclick="viewOfficerDetails(${officer.userId})">
			            👁️ View Details
			        </button>
			    </td>
			`;

            tableBody.appendChild(row);
        });

    } catch (error) {

        console.error("Loan officers error:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Unable to load loan officers.
                </td>
            </tr>
        `;
    }
}
async function loadAllLoans() {

    const tableBody =
        document.getElementById("allLoansTableBody");

    tableBody.innerHTML = `
        <tr>
            <td colspan="12">Loading loans...</td>
        </tr>
    `;

    try {

        const response = await fetch(
            "/api/admin/loans",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                alert("You are not authorized to view loans.");
                return;
            }

            throw new Error("Failed to load loans");
        }

        const loans = await response.json();

        if (loans.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="12">
                        No loans found.
                    </td>
                </tr>
            `;

            return;
        }

        tableBody.innerHTML = "";

        loans.forEach(loan => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${loan.loanId}</td>
                <td>${loan.userId}</td>
                <td>${loan.customerName || "-"}</td>
                <td>${loan.customerEmail || "-"}</td>
                <td>${loan.loanType || "-"}</td>
                <td>₹${loan.amount || 0}</td>
                <td>${loan.interestRate || 0}%</td>
                <td>${loan.tenureMonths || 0} months</td>
                <td>₹${loan.emiAmount || 0}</td>
                <td>${loan.status || "-"}</td>
                <td>${loan.appliedDate || "-"}</td>
                <td>${loan.approvedDate || "-"}</td>
				<td>
				    <button
				        class="primary-btn"
				        onclick="viewLoanDetails(${loan.loanId})">
				        👁️ View Details
				    </button>
				</td>
            `;

            tableBody.appendChild(row);
        });

    } catch (error) {

        console.error("All loans error:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="13">
                    Unable to load loans.
                </td>
            </tr>
        `;
    }
}
async function viewLoanDetails(loanId) {

    const modal =
        document.getElementById("loanDetailsModal");

    const content =
        document.getElementById("loanDetailsContent");

    // Open modal
    modal.style.display = "flex";

    content.innerHTML = "Loading loan details...";

    try {

        const currentToken =
            localStorage.getItem("token");

        if (!currentToken) {
            alert("Session expired. Please login again.");
            logout();
            return;
        }

        /*
         * We already have all loan information
         * from /api/admin/loans.
         *
         * Find the selected loan from the
         * loaded table data.
         */

        const response = await fetch(
            "/api/admin/loans",
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + currentToken
                }
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                alert("Session expired. Please login again.");
                logout();
                return;
            }

            if (response.status === 403) {
                content.innerHTML = `
                    <p class="error-message">
                        You are not authorized to view loan details.
                    </p>
                `;
                return;
            }

            throw new Error(
                "Failed to load loan details"
            );
        }

        const loans = await response.json();

        const loan =
            loans.find(
                item => item.loanId === loanId
            );

        if (!loan) {

            content.innerHTML = `
                <p class="error-message">
                    Loan details not found.
                </p>
            `;

            return;
        }

        content.innerHTML = `

            <div class="loan-details-card">

                <h3>
                    Loan #${loan.loanId}
                </h3>

                <div class="loan-details-grid">

                    <div>
                        <strong>Customer ID:</strong>
                        ${loan.userId}
                    </div>

                    <div>
                        <strong>Customer Name:</strong>
                        ${loan.customerName || "-"}
                    </div>

                    <div>
                        <strong>Customer Email:</strong>
                        ${loan.customerEmail || "-"}
                    </div>

                    <div>
                        <strong>Loan Type:</strong>
                        ${loan.loanType || "-"}
                    </div>

                    <div>
                        <strong>Loan Amount:</strong>
                        ₹${loan.amount || 0}
                    </div>

                    <div>
                        <strong>Interest Rate:</strong>
                        ${loan.interestRate || 0}%
                    </div>

                    <div>
                        <strong>Tenure:</strong>
                        ${loan.tenureMonths || 0} months
                    </div>

                    <div>
                        <strong>EMI Amount:</strong>
                        ₹${loan.emiAmount || 0}
                    </div>

                    <div>
                        <strong>Status:</strong>
                        ${loan.status || "-"}
                    </div>

                    <div>
                        <strong>Applied Date:</strong>
                        ${loan.appliedDate || "-"}
                    </div>

                    <div>
                        <strong>Approved Date:</strong>
                        ${loan.approvedDate || "-"}
                    </div>

                </div>

            </div>
        `;

    } catch (error) {

        console.error(
            "Loan details error:",
            error
        );

        content.innerHTML = `
            <p class="error-message">
                Unable to load loan details.
            </p>
        `;
    }
}


function closeLoanDetailsModal() {

    const modal =
        document.getElementById("loanDetailsModal");

    modal.style.display = "none";
}

// ===============================
// LOGOUT
// ===============================

function logout() {

    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("name");
    localStorage.removeItem("role");

    window.location.href = "/index.html";
}


// ===============================
// INITIAL LOAD
// ===============================

loadDashboard();

document.getElementById("createOfficerForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const message =
            document.getElementById("createOfficerMessage");

        const name =
            document.getElementById("officerName").value.trim();

        const email =
            document.getElementById("officerEmail").value.trim();

        const phone =
            document.getElementById("officerPhone").value.trim();

        const password =
            document.getElementById("officerPassword").value;

        message.innerHTML = "Creating loan officer...";

        try {

            const response = await fetch(
                "/api/admin/loan-officers",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },

                    body: JSON.stringify({
                        name: name,
                        email: email,
                        phone: phone,
                        password: password
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {

                message.innerHTML = `
                    <p class="error-message">
                        ${data.message || "Failed to create loan officer."}
                    </p>
                `;

                return;
            }

            message.innerHTML = `
                <p class="success-message">
                    Loan officer created successfully.
                </p>
            `;

            document.getElementById("createOfficerForm").reset();

            loadLoanOfficers();

        } catch (error) {

            console.error("Create officer error:", error);

            message.innerHTML = `
                <p class="error-message">
                    Unable to create loan officer.
                </p>
            `;
        }
    });
	
	// ===============================
	// VIEW LOAN OFFICER DETAILS
	// ===============================

	async function viewOfficerDetails(officerId) {

	    const modal =
	        document.getElementById("officerDetailsModal");

	    const content =
	        document.getElementById("officerDetailsContent");

	    modal.style.display = "flex";

	    content.innerHTML = "Loading officer details...";

	    try {

	        const currentToken =
	            localStorage.getItem("token");

	        if (!currentToken) {
	            alert("Session expired. Please login again.");
	            logout();
	            return;
	        }

	        const response = await fetch(
	            "/api/admin/loan-officers",
	            {
	                method: "GET",
	                headers: {
	                    "Authorization":
	                        "Bearer " + currentToken
	                }
	            }
	        );

	        if (!response.ok) {

	            if (response.status === 401) {
	                alert("Session expired. Please login again.");
	                logout();
	                return;
	            }

	            if (response.status === 403) {

	                content.innerHTML = `
	                    <p class="error-message">
	                        You are not authorized to view officer details.
	                    </p>
	                `;

	                return;
	            }

	            throw new Error(
	                "Failed to load officer details"
	            );
	        }

	        const officers =
	            await response.json();

	        const officer =
	            officers.find(
	                item => item.userId === officerId
	            );

	        if (!officer) {

	            content.innerHTML = `
	                <p class="error-message">
	                    Officer details not found.
	                </p>
	            `;

	            return;
	        }

	        content.innerHTML = `

	            <div class="loan-details-card">

	                <h3>
	                    👨‍💼 ${officer.name || "-"}
	                </h3>

	                <div class="loan-details-grid">

	                    <div>
	                        <strong>Officer ID:</strong>
	                        ${officer.userId || "-"}
	                    </div>

	                    <div>
	                        <strong>Name:</strong>
	                        ${officer.name || "-"}
	                    </div>

	                    <div>
	                        <strong>Email:</strong>
	                        ${officer.email || "-"}
	                    </div>

	                    <div>
	                        <strong>Phone:</strong>
	                        ${officer.phone || "-"}
	                    </div>

	                    <div>
	                        <strong>Role:</strong>
	                        ${officer.role || "-"}
	                    </div>

	                </div>

	            </div>
	        `;

	    } catch (error) {

	        console.error(
	            "Officer details error:",
	            error
	        );

	        content.innerHTML = `
	            <p class="error-message">
	                Unable to load officer details.
	            </p>
	        `;
	    }
	}


	function closeOfficerDetailsModal() {

	    const modal =
	        document.getElementById("officerDetailsModal");

	    modal.style.display = "none";
	}