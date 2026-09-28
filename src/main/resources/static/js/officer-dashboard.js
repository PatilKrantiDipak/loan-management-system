const token = localStorage.getItem("token");
const officerName = localStorage.getItem("name");
const role = localStorage.getItem("role");

// ===============================
// CHECK LOGIN
// ===============================

if (!token || role !== "LOAN_OFFICER") {
    window.location.href = "/index.html";
}

// ===============================
// DISPLAY OFFICER NAME
// ===============================

document.getElementById("officerName").textContent =
    officerName || "Loan Officer";

// ===============================
// LOGOUT
// ===============================

document.getElementById("logoutBtn")
    .addEventListener("click", function () {

        localStorage.clear();
        window.location.href = "/index.html";

    });

// ===============================
// LOAD ALL LOANS
// ===============================

async function loadLoans() {

    const loanContent =
        document.getElementById("loanContent");

    loanContent.innerHTML =
        "<p>Loading loan applications...</p>";

    try {

        const response = await fetch("/api/loans", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {

            loanContent.innerHTML =
                `<p class="error">
                    Unable to load loan applications.
                    Status: ${response.status}
                </p>`;

            return;
        }

        const loans = await response.json();

        console.log("Loans received from backend:", loans);

        if (loans.length === 0) {

            loanContent.innerHTML =
                `<p>No loan applications found.</p>`;

            return;
        }

        displayLoans(loans);

    } catch (error) {

        console.error(error);

        loanContent.innerHTML =
            `<p class="error">
                Unable to connect to server.
            </p>`;
    }
}

// ===============================
// DISPLAY LOANS
// ===============================

function displayLoans(loans) {

    const loanContent =
        document.getElementById("loanContent");

    let html = `

        <div class="table-container">

            <table>

                <thead>

                    <tr>
                        <th>Loan ID</th>
                        <th>Customer ID</th>
                        <th>Customer Name</th>
                        <th>Customer Email</th>
                        <th>Loan Type</th>
                        <th>Amount</th>
                        <th>Interest</th>
                        <th>Tenure</th>
                        <th>EMI</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>

                </thead>

                <tbody>
    `;

    loans.forEach(loan => {

        html += `

            <tr>

                <td>
                    ${loan.loanId}
                </td>

                <td>
                    ${loan.userId}
                </td>

                <td>
                    ${loan.customerName || "-"}
                </td>

                <td>
                    ${loan.customerEmail || "-"}
                </td>

                <td>
                    ${loan.loanType}
                </td>

                <td>
                    ₹${loan.amount}
                </td>

                <td>
                    ${loan.interestRate}%
                </td>

                <td>
                    ${loan.tenureMonths} months
                </td>

                <td>
                    ${loan.emiAmount
                        ? "₹" + loan.emiAmount
                        : "-"}
                </td>

                <td>

                    <span class="status">

                        ${loan.status}

                    </span>

                </td>

                <td>

                    ${
                        loan.status === "APPLIED"

                        ?

                        `
                        <button
                            class="approve-btn"
                            onclick="approveLoan(${loan.loanId})">

                            Approve

                        </button>

                        <button
                            class="reject-btn"
                            onclick="rejectLoan(${loan.loanId})">

                            Reject

                        </button>
                        `

                        :

                        "No Action"
                    }

                </td>

            </tr>

        `;

    });

    html += `

                </tbody>

            </table>

        </div>

    `;

    loanContent.innerHTML = html;
}

// ===============================
// APPROVE LOAN
// ===============================

async function approveLoan(loanId) {

    const confirmed =
        confirm(
            "Are you sure you want to approve this loan?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `/api/loans/${loanId}/approve`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        const data = await response.json();

        if (response.ok) {

            alert(
                `Loan ${loanId} approved successfully`
            );

            loadLoans();

        } else {

            alert(
                data.message ||
                "Unable to approve loan"
            );
        }

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server"
        );
    }
}

// ===============================
// REJECT LOAN
// ===============================

async function rejectLoan(loanId) {

    const confirmed =
        confirm(
            "Are you sure you want to reject this loan?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `/api/loans/${loanId}/reject`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        const data = await response.json();

        if (response.ok) {

            alert(
                `Loan ${loanId} rejected successfully`
            );

            loadLoans();

        } else {

            alert(
                data.message ||
                "Unable to reject loan"
            );
        }

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server"
        );
    }
}

// ===============================
// REFRESH LOANS
// ===============================

document.getElementById("refreshBtn")
    .addEventListener("click", loadLoans);

// ===============================
// REFRESH DOCUMENTS
// ===============================

document.getElementById("refreshDocumentsBtn")
    .addEventListener("click", loadDocuments);

// ===============================
// LOAD ALL DOCUMENTS
// ===============================

async function loadDocuments() {

    const documentContent =
        document.getElementById("documentContent");

    documentContent.innerHTML =
        "<p>Loading documents...</p>";

    try {

        const response = await fetch(
            "/api/documents/all",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            documentContent.innerHTML =
                `<p class="error">
                    Unable to load documents.
                    Status: ${response.status}
                </p>`;

            return;
        }

        const documents = await response.json();

        displayDocuments(documents);

    } catch (error) {

        console.error(error);

        documentContent.innerHTML =
            `<p class="error">
                Unable to connect to server.
            </p>`;
    }
}

// ===============================
// DISPLAY DOCUMENTS
// ===============================

function displayDocuments(documents) {

    const documentContent =
        document.getElementById("documentContent");

    if (documents.length === 0) {

        documentContent.innerHTML =
            "<p>No customer documents found.</p>";

        return;
    }

    let html = `

        <div class="table-container">

            <table>

                <thead>

                    <tr>

                        <th>Customer ID</th>
                        <th>Document ID</th>
                        <th>Document Type</th>
                        <th>File Name</th>
                        <th>Status</th>
                        <th>View</th>
                        <th>Action</th>

                    </tr>

                </thead>

                <tbody>

    `;

    documents.forEach(doc => {

        html += `

            <tr>

                <td>
                    ${doc.userId}
                </td>

                <td>
                    ${doc.documentId}
                </td>

                <td>
                    ${doc.documentType}
                </td>

                <td>
                    ${doc.fileName}
                </td>

                <td>

                    <span class="status
                        ${doc.verificationStatus.toLowerCase()}">

                        ${doc.verificationStatus}

                    </span>

                </td>

                <td>

                    <button
                        onclick="viewDocument(${doc.documentId})">

                        View

                    </button>

                </td>

                <td>
        `;

        if (doc.verificationStatus === "PENDING") {

            html += `

                <button
                    class="approve-btn"
                    onclick="verifyDocument(${doc.documentId})">

                    Verify

                </button>

                <button
                    class="reject-btn"
                    onclick="rejectDocument(${doc.documentId})">

                    Reject

                </button>

            `;

        } else {

            html += "No Action";
        }

        html += `

                </td>

            </tr>

        `;
    });

    html += `

                </tbody>

            </table>

        </div>

    `;

    documentContent.innerHTML = html;
}

// ===============================
// VIEW DOCUMENT
// ===============================

async function viewDocument(documentId) {

    try {

        const response = await fetch(
            `/api/documents/${documentId}/view`,
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            alert(
                "Unable to view document. Status: "
                + response.status
            );

            return;
        }

        const blob = await response.blob();

        const fileUrl =
            URL.createObjectURL(blob);

        window.open(fileUrl, "_blank");

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server."
        );
    }
}

// ===============================
// VERIFY DOCUMENT
// ===============================

async function verifyDocument(documentId) {

    const confirmed =
        confirm(
            "Are you sure you want to verify this document?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `/api/documents/${documentId}/verify`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        const result = await response.json();

        if (!response.ok) {

            alert(
                result.message ||
                "Unable to verify document."
            );

            return;
        }

        alert(
            "Document verified successfully."
        );

        loadDocuments();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server."
        );
    }
}

// ===============================
// REJECT DOCUMENT
// ===============================

async function rejectDocument(documentId) {

    const confirmed =
        confirm(
            "Are you sure you want to reject this document?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `/api/documents/${documentId}/reject`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        const result = await response.json();

        if (!response.ok) {

            alert(
                result.message ||
                "Unable to reject document."
            );

            return;
        }

        alert(
            "Document rejected successfully."
        );

        loadDocuments();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server."
        );
    }
}

// ===============================
// INITIAL LOAD
// ===============================

loadLoans();