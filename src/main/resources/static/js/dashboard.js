// =====================================================
// CUSTOMER DETAILS
// =====================================================

const token =
    localStorage.getItem("token");

const userName =
    localStorage.getItem("name");

const userId =
    localStorage.getItem("userId");

const role =
    localStorage.getItem("role");


// =====================================================
// CUSTOMER LOGIN CHECK
// =====================================================

if (!token || role !== "CUSTOMER") {

    window.location.href =
        "/index.html";

}


// =====================================================
// DISPLAY CUSTOMER DETAILS
// =====================================================

const userNameElement =
    document.getElementById("userName");

if (userNameElement) {

    userNameElement.textContent =
        userName || "Customer";

}


const customerIdElement =
    document.getElementById("customerId");

if (customerIdElement) {

    customerIdElement.textContent =
        userId || "-";

}


// =====================================================
// LOGOUT
// =====================================================

const logoutBtn =
    document.getElementById("logoutBtn");

if (logoutBtn) {

    logoutBtn.addEventListener(
        "click",
        function () {

            localStorage.removeItem("token");
            localStorage.removeItem("userId");
            localStorage.removeItem("name");
            localStorage.removeItem("email");
            localStorage.removeItem("role");

            window.location.href =
                "/index.html";

        }
    );

}


// =====================================================
// APPLY LOAN BUTTON
// =====================================================

const applyLoanBtn =
    document.getElementById("applyLoanBtn");

if (applyLoanBtn) {

    applyLoanBtn.addEventListener(
        "click",
        function () {

            showLoanApplicationForm();

        }
    );

}


// =====================================================
// MY LOANS BUTTON
// =====================================================

const myLoansBtn =
    document.getElementById("myLoansBtn");

if (myLoansBtn) {

    myLoansBtn.addEventListener(
        "click",
        function () {

            loadMyLoans();

        }
    );

}


// =====================================================
// DOCUMENTS BUTTON
// =====================================================

const documentsBtn =
    document.getElementById("documentsBtn");

if (documentsBtn) {

    documentsBtn.addEventListener(
        "click",
        function () {

            showDocumentsSection();

        }
    );

}


// =====================================================
// PROFILE BUTTON
// =====================================================

const profileBtn =
    document.getElementById("profileBtn");

if (profileBtn) {

    profileBtn.addEventListener(
        "click",
        function () {

            showProfileSection();

        }
    );

}


// =====================================================
// CHANGE PASSWORD BUTTON
// =====================================================

const changePasswordBtn =
    document.getElementById(
        "changePasswordBtn"
    );

if (changePasswordBtn) {

    changePasswordBtn.addEventListener(
        "click",
        function () {

            showChangePasswordSection();

        }
    );

}


// =====================================================
// SHOW LOAN APPLICATION FORM
// =====================================================

function showLoanApplicationForm() {

    const content =
        document.getElementById("content");

    content.innerHTML = `

        <div class="loan-form">

            <h2>
                Apply for Loan
            </h2>

            <label>
                Loan Type
            </label>

            <select id="loanType">

                <option value="PERSONAL">
                    Personal Loan
                </option>

                <option value="HOME">
                    Home Loan
                </option>

                <option value="EDUCATION">
                    Education Loan
                </option>

                <option value="VEHICLE">
                    Vehicle Loan
                </option>

                <option value="BUSINESS">
                    Business Loan
                </option>

            </select>

            <br><br>

            <label>
                Loan Amount
            </label>

            <input
                type="number"
                id="loanAmount"
                placeholder="Enter loan amount"
            >

            <br><br>

            <label>
                Interest Rate (%)
            </label>

            <input
                type="number"
                step="0.01"
                id="interestRate"
                placeholder="Enter interest rate"
            >

            <br><br>

            <label>
                Tenure (Months)
            </label>

            <input
                type="number"
                id="tenureMonths"
                placeholder="Enter tenure in months"
            >

            <br><br>

            <button id="submitLoanBtn">

                Submit Loan Application

            </button>

            <div id="loanMessage"></div>

        </div>
    `;


    document.getElementById(
        "submitLoanBtn"
    ).addEventListener(
        "click",
        submitLoan
    );

}


// =====================================================
// SUBMIT LOAN
// =====================================================

async function submitLoan() {

    const loanType =
        document.getElementById(
            "loanType"
        ).value;

    const amount =
        document.getElementById(
            "loanAmount"
        ).value;

    const interestRate =
        document.getElementById(
            "interestRate"
        ).value;

    const tenureMonths =
        document.getElementById(
            "tenureMonths"
        ).value;

    const message =
        document.getElementById(
            "loanMessage"
        );


    const loanData = {

        loanType: loanType,

        amount: Number(amount),

        interestRate: Number(interestRate),

        tenureMonths: Number(tenureMonths)

    };


    message.innerHTML =
        "<p>Submitting loan application...</p>";


    try {

        const response =
            await fetch(
                "/api/loans",
                {
                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
                        JSON.stringify(
                            loanData
                        )

                }
            );


        const result =
            await response.json();


        if (!response.ok) {

            message.innerHTML = `

                <p class="error">

                    ${
                        result.message ||
                        "Unable to submit loan application."
                    }

                </p>

            `;

            return;

        }


        message.innerHTML = `

            <p class="success">

                Loan application submitted successfully.

            </p>

            <p>

                Loan ID:

                <strong>
                    ${result.loanId}
                </strong>

            </p>

            <p>

                Status:

                <strong>
                    ${result.status}
                </strong>

            </p>

        `;


        document.getElementById(
            "loanAmount"
        ).value = "";

        document.getElementById(
            "interestRate"
        ).value = "";

        document.getElementById(
            "tenureMonths"
        ).value = "";


    } catch (error) {

        console.error(error);


        message.innerHTML = `

            <p class="error">

                Unable to connect to server.

            </p>

        `;

    }

}


// =====================================================
// LOAD MY LOANS
// =====================================================

async function loadMyLoans() {

    const content =
        document.getElementById(
            "content"
        );


    content.innerHTML = `

        <div class="loan-list">

            <h2>
                My Loan Applications
            </h2>

            <p>
                Loading loans...
            </p>

        </div>

    `;


    try {

        const response =
            await fetch(
                "/api/loans/my",
                {
                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        const loans =
            await response.json();


        if (!response.ok) {

            content.innerHTML = `

                <div class="loan-list">

                    <p class="error">

                        ${
                            loans.message ||
                            "Unable to load loans."
                        }

                    </p>

                </div>

            `;

            return;

        }


        displayLoans(loans);


    } catch (error) {

        console.error(error);


        content.innerHTML = `

            <div class="loan-list">

                <p class="error">

                    Unable to connect to server.

                </p>

            </div>

        `;

    }

}


// =====================================================
// DISPLAY LOANS
// =====================================================

function displayLoans(loans) {

    const content =
        document.getElementById(
            "content"
        );


    if (loans.length === 0) {

        content.innerHTML = `

            <div class="loan-list">

                <h2>
                    My Loans
                </h2>

                <p>
                    No loan applications found.
                </p>

            </div>

        `;

        return;

    }


    let html = `

        <div class="loan-list">

            <h2>
                My Loan Applications
            </h2>

            <div class="table-container">

                <table>

                    <thead>

                        <tr>

                            <th>Loan ID</th>

                            <th>Customer ID</th>

                            <th>Loan Type</th>

                            <th>Amount</th>

                            <th>Interest Rate</th>

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
                    ${loan.userId || userId}
                </td>

                <td>
                    ${loan.loanType}
                </td>

                <td>
                    ₹ ${loan.amount}
                </td>

                <td>
                    ${loan.interestRate}%
                </td>

                <td>
                    ${loan.tenureMonths} months
                </td>

                <td>

                    ${
                        loan.emiAmount
                            ? "₹ " + loan.emiAmount
                            : "-"
                    }

                </td>

                <td>

                    <span class="status
                        ${loan.status.toLowerCase()}">

                        ${loan.status}

                    </span>

                </td>

                <td>

        `;


        if (
            loan.status === "APPROVED"
        ) {

            html += `

                <button
                    onclick="viewRepaymentSchedule(
                        ${loan.loanId}
                    )">

                    View Schedule

                </button>

            `;

        } else {

            html += "-";

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

        </div>

        <div id="repaymentContent"></div>

    `;


    content.innerHTML =
        html;

}


// =====================================================
// VIEW REPAYMENT SCHEDULE
// =====================================================

async function viewRepaymentSchedule(
    loanId
) {

    const repaymentContent =
        document.getElementById(
            "repaymentContent"
        );


    if (!repaymentContent) {

        return;

    }


    repaymentContent.innerHTML = `

        <div class="loan-list">

            <h2>
                Repayment Schedule
            </h2>

            <p>
                Loading repayment schedule...
            </p>

        </div>

    `;


    try {

        const response =
            await fetch(
                `/api/repayments/loan/${loanId}`,
                {
                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        const repayments =
            await response.json();


        if (!response.ok) {

            repaymentContent.innerHTML = `

                <div class="loan-list">

                    <p class="error">

                        ${
                            repayments.message ||
                            "Unable to load repayment schedule."
                        }

                    </p>

                </div>

            `;

            return;

        }


        if (repayments.length === 0) {

            repaymentContent.innerHTML = `

                <div class="loan-list">

                    <h2>
                        Repayment Schedule
                    </h2>

                    <p>
                        No repayment schedule found.
                    </p>

                </div>

            `;

            return;

        }


        const paidCount =
            repayments.filter(
                r => r.status === "PAID"
            ).length;


        const pendingCount =
            repayments.filter(
                r => r.status === "PENDING"
            ).length;


        const overdueCount =
            repayments.filter(
                r => r.status === "OVERDUE"
            ).length;


        const totalEmi =
            repayments.reduce(
                (total, r) =>
                    total +
                    Number(r.emiAmount),
                0
            );


        const remainingAmount =
            repayments
                .filter(
                    r => r.status !== "PAID"
                )
                .reduce(
                    (total, r) =>
                        total +
                        Number(r.emiAmount),
                    0
                );


        let html = `

            <div class="loan-list">

                <h2>
                    Repayment Schedule
                    - Loan #${loanId}
                </h2>


                <div class="dashboard-actions">

                    <div class="action-card">

                        <h3>
                            Total Installments
                        </h3>

                        <h2>
                            ${repayments.length}
                        </h2>

                    </div>


                    <div class="action-card">

                        <h3>
                            Paid
                        </h3>

                        <h2>
                            ${paidCount}
                        </h2>

                    </div>


                    <div class="action-card">

                        <h3>
                            Pending
                        </h3>

                        <h2>
                            ${pendingCount}
                        </h2>

                    </div>


                    <div class="action-card">

                        <h3>
                            Overdue
                        </h3>

                        <h2>
                            ${overdueCount}
                        </h2>

                    </div>

                </div>


                <p>

                    <strong>
                        Total Scheduled Amount:
                    </strong>

                    ₹ ${totalEmi.toFixed(2)}

                </p>


                <p>

                    <strong>
                        Remaining Amount:
                    </strong>

                    ₹ ${remainingAmount.toFixed(2)}

                </p>


                <div class="table-container">

                    <table>

                        <thead>

                            <tr>

                                <th>Installment</th>

                                <th>Due Date</th>

                                <th>EMI</th>

                                <th>Principal</th>

                                <th>Interest</th>

                                <th>Payment Date</th>

                                <th>Status</th>

                            </tr>

                        </thead>

                        <tbody>

        `;


        repayments.forEach(
            repayment => {

                html += `

                    <tr>

                        <td>
                            ${repayment.installmentNumber}
                        </td>

                        <td>
                            ${repayment.dueDate}
                        </td>

                        <td>
                            ₹ ${repayment.emiAmount}
                        </td>

                        <td>
                            ₹ ${repayment.principalAmount}
                        </td>

                        <td>
                            ₹ ${repayment.interestAmount}
                        </td>

                        <td>

                            ${
                                repayment.paymentDate
                                    ? repayment.paymentDate
                                    : "-"
                            }

                        </td>

                        <td>

                            <span class="status
                                ${repayment.status.toLowerCase()}">

                                ${repayment.status}

                            </span>


                            ${
                                repayment.status === "PENDING"
                                    ? `

                                        <br><br>

                                        <button
                                            onclick="payInstallment(
                                                ${repayment.repaymentId}
                                            )">

                                            Pay EMI

                                        </button>

                                      `
                                    : ""
                            }


                            ${
                                repayment.status === "OVERDUE"
                                    ? `

                                        <br><br>

                                        <button
                                            onclick="payInstallment(
                                                ${repayment.repaymentId}
                                            )">

                                            Pay Overdue EMI

                                        </button>

                                      `
                                    : ""
                            }

                        </td>

                    </tr>

                `;

            }
        );


        html += `

                        </tbody>

                    </table>

                </div>

            </div>

        `;


        repaymentContent.innerHTML =
            html;


    } catch (error) {

        console.error(error);


        repaymentContent.innerHTML = `

            <div class="loan-list">

                <p class="error">

                    Unable to connect to server.

                </p>

            </div>

        `;

    }

}


// =====================================================
// PAY INSTALLMENT
// =====================================================

async function payInstallment(
    repaymentId
) {

    const confirmPayment =
        confirm(
            "Are you sure you want to pay this EMI?"
        );


    if (!confirmPayment) {

        return;

    }


    try {

        const response =
            await fetch(
                `/api/repayments/${repaymentId}/pay`,
                {
                    method: "PUT",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        const result =
            await response.json();


        if (!response.ok) {

            alert(
                result.message ||
                "Unable to process payment."
            );

            return;

        }


        alert(
            "EMI paid successfully!"
        );


        viewRepaymentSchedule(
            result.loanId
        );


    } catch (error) {

        console.error(error);


        alert(
            "Unable to connect to server."
        );

    }

}


// =====================================================
// DOCUMENT SECTION
// =====================================================

function showDocumentsSection() {

    const content =
        document.getElementById(
            "content"
        );


    content.innerHTML = `

        <div class="loan-list">

            <h2>
                My KYC Documents
            </h2>

            <p>

                Upload your KYC documents once.

                After verification, they can be reused
                for future loan applications.

            </p>

            <hr>

            <h3>
                Upload Document
            </h3>


            <label>
                Document Type
            </label>


            <select id="documentType">

                <option value="PAN">
                    PAN
                </option>

                <option value="AADHAAR">
                    Aadhaar
                </option>

                <option value="ADDRESS_PROOF">
                    Address Proof
                </option>

                <option value="INCOME_PROOF">
                    Income Proof
                </option>

                <option value="BANK_STATEMENT">
                    Bank Statement
                </option>

            </select>


            <br><br>


            <label>
                Select Document
            </label>


            <input
                type="file"
                id="documentFile"
                accept=".pdf,.jpg,.jpeg,.png"
                required
            >


            <br><br>


            <button id="uploadDocumentBtn">

                Upload Document

            </button>


            <div id="uploadMessage"></div>


            <hr>


            <h3>
                My Documents
            </h3>


            <div id="documentList">

                Loading documents...

            </div>

        </div>

    `;


    document.getElementById(
        "uploadDocumentBtn"
    ).addEventListener(
        "click",
        uploadDocument
    );


    loadCustomerDocuments();

}


// =====================================================
// UPLOAD DOCUMENT
// =====================================================

async function uploadDocument() {

    const documentType =
        document.getElementById(
            "documentType"
        ).value;


    const file =
        document.getElementById(
            "documentFile"
        ).files[0];


    const message =
        document.getElementById(
            "uploadMessage"
        );


    if (!file) {

        message.innerHTML = `

            <p class="error">

                Please select a document.

            </p>

        `;

        return;

    }


    const formData =
        new FormData();


    formData.append(
        "documentType",
        documentType
    );


    formData.append(
        "file",
        file
    );


    message.innerHTML =
        "<p>Uploading document...</p>";


    try {

        const response =
            await fetch(
                "/api/documents/upload",
                {
                    method: "POST",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    },

                    body: formData

                }
            );


        const result =
            await response.json();


        if (!response.ok) {

            message.innerHTML = `

                <p class="error">

                    ${
                        result.message ||
                        "Unable to upload document."
                    }

                </p>

            `;

            return;

        }


        message.innerHTML = `

            <p class="success">

                Document uploaded successfully.

            </p>

        `;


        document.getElementById(
            "documentFile"
        ).value = "";


        loadCustomerDocuments();


    } catch (error) {

        console.error(error);


        message.innerHTML = `

            <p class="error">

                Unable to connect to server.

            </p>

        `;

    }

}


// =====================================================
// LOAD CUSTOMER DOCUMENTS
// =====================================================

async function loadCustomerDocuments() {

    const documentList =
        document.getElementById(
            "documentList"
        );


    try {

        const response =
            await fetch(
                "/api/documents/my",
                {
                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        const documents =
            await response.json();


        if (!response.ok) {

            documentList.innerHTML = `

                <p class="error">

                    ${
                        documents.message ||
                        "Unable to load documents."
                    }

                </p>

            `;

            return;

        }


        if (documents.length === 0) {

            documentList.innerHTML = `

                <p>
                    No documents uploaded yet.
                </p>

            `;

            return;

        }


        let html = `

            <div class="table-container">

                <table>

                    <thead>

                        <tr>

                            <th>Document Type</th>

                            <th>File Name</th>

                            <th>Status</th>

                            <th>Uploaded Date</th>

                            <th>Verified Date</th>

                            <th>Action</th>

                        </tr>

                    </thead>

                    <tbody>

        `;


        documents.forEach(
            doc => {

                html += `

                    <tr>

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
                            ${doc.uploadedDate}
                        </td>

                        <td>

                            ${
                                doc.verifiedDate
                                    ? doc.verifiedDate
                                    : "-"
                            }

                        </td>

                        <td>

                `;


                if (
                    doc.verificationStatus ===
                    "REJECTED"
                ) {

                    html += `

                        <button
                            class="replace-btn"
                            onclick="replaceDocument(
                                ${doc.documentId}
                            )">

                            Replace

                        </button>

                    `;

                } else {

                    html += "-";

                }


                html += `

                        </td>

                    </tr>

                `;

            }
        );


        html += `

                    </tbody>

                </table>

            </div>

        `;


        documentList.innerHTML =
            html;


    } catch (error) {

        console.error(error);


        documentList.innerHTML = `

            <p class="error">

                Unable to connect to server.

            </p>

        `;

    }

}


// =====================================================
// REPLACE REJECTED DOCUMENT
// =====================================================

async function replaceDocument(
    documentId
) {

    const fileInput =
        document.createElement(
            "input"
        );


    fileInput.type = "file";


    fileInput.accept =
        ".pdf,.jpg,.jpeg,.png";


    fileInput.click();


    fileInput.addEventListener(
        "change",
        async function () {

            const file =
                fileInput.files[0];


            if (!file) {

                return;

            }


            const formData =
                new FormData();


            formData.append(
                "file",
                file
            );


            try {

                const response =
                    await fetch(
                        `/api/documents/replace/${documentId}`,
                        {
                            method: "POST",

                            headers: {

                                "Authorization":
                                    "Bearer " + token

                            },

                            body: formData

                        }
                    );


                const result =
                    await response.json();


                if (!response.ok) {

                    alert(
                        result.message ||
                        "Unable to replace document."
                    );

                    return;

                }


                alert(
                    "Document replaced successfully. " +
                    "It is now pending verification."
                );


                loadCustomerDocuments();


            } catch (error) {

                console.error(error);


                alert(
                    "Unable to connect to server."
                );

            }

        }
    );

}


// =====================================================
// SHOW PROFILE
// =====================================================

async function showProfileSection() {

    const content =
        document.getElementById(
            "content"
        );


    content.innerHTML = `

        <div class="loan-list">

            <h2>
                👤 My Profile
            </h2>

            <p>
                Loading profile...
            </p>

        </div>

    `;


    try {

        const response =
            await fetch(
                "/api/users/profile",
                {
                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        if (!response.ok) {

            if (response.status === 401) {

                alert(
                    "Session expired. Please login again."
                );

                localStorage.clear();

                window.location.href =
                    "/index.html";

                return;

            }


            throw new Error(
                "Unable to load profile"
            );

        }


        const profile =
            await response.json();


        content.innerHTML = `

            <div class="loan-list">

                <h2>
                    👤 My Profile
                </h2>


                <div class="loan-details-card">

                    <div class="loan-details-grid">

                        <div>

                            <strong>
                                User ID:
                            </strong>

                            ${profile.id || userId}

                        </div>


                        <div>

                            <strong>
                                Name:
                            </strong>

                            ${profile.name || "-"}

                        </div>


                        <div>

                            <strong>
                                Email:
                            </strong>

                            ${profile.email || "-"}

                        </div>


                        <div>

                            <strong>
                                Phone:
                            </strong>

                            ${profile.phone || "-"}

                        </div>


                        <div>

                            <strong>
                                Role:
                            </strong>

                            ${profile.role || "-"}

                        </div>

                    </div>


                    <br>


                    <button
                        onclick="showEditProfileForm()">

                        ✏️ Edit Profile

                    </button>


                    <button
                        onclick="showChangePasswordSection()">

                        🔐 Change Password

                    </button>

                </div>

            </div>

        `;


    } catch (error) {

        console.error(
            "Profile error:",
            error
        );


        content.innerHTML = `

            <div class="loan-list">

                <p class="error">

                    Unable to load profile.

                </p>

            </div>

        `;

    }

}


// =====================================================
// EDIT PROFILE
// =====================================================

function showEditProfileForm() {

    const content =
        document.getElementById(
            "content"
        );


    content.innerHTML = `

        <div class="loan-list">

            <h2>
                ✏️ Edit Profile
            </h2>


            <div class="loan-details-card">

                <form id="editProfileForm">


                    <div class="form-group">

                        <label>
                            Name
                        </label>

                        <input
                            type="text"
                            id="editName"
                            value="${userName || ""}"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>
                            Phone
                        </label>

                        <input
                            type="text"
                            id="editPhone"
                            value=""
                            maxlength="10"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>
                            Email
                        </label>

                        <input
                            type="email"
                            value="${
                                localStorage.getItem("email") || ""
                            }"
                            disabled
                        >

                    </div>


                    <div class="form-group">

                        <label>
                            Role
                        </label>

                        <input
                            type="text"
                            value="${role || ""}"
                            disabled
                        >

                    </div>


                    <button
                        type="submit"
                        class="primary-btn">

                        💾 Save Changes

                    </button>


                    <button
                        type="button"
                        class="secondary-btn"
                        onclick="showProfileSection()">

                        Cancel

                    </button>


                </form>

            </div>

        </div>

    `;


    loadCurrentPhone();


    document
        .getElementById(
            "editProfileForm"
        )
        .addEventListener(
            "submit",
            updateProfile
        );

}


// =====================================================
// LOAD CURRENT PHONE
// =====================================================

async function loadCurrentPhone() {

    try {

        const response =
            await fetch(
                "/api/users/profile",
                {
                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to load profile"
            );

        }


        const profile =
            await response.json();


        const phoneElement =
            document.getElementById(
                "editPhone"
            );


        if (phoneElement) {

            phoneElement.value =
                profile.phone || "";

        }


    } catch (error) {

        console.error(
            "Load phone error:",
            error
        );

    }

}


// =====================================================
// UPDATE PROFILE
// =====================================================

async function updateProfile(event) {

    event.preventDefault();


    const name =
        document.getElementById(
            "editName"
        ).value.trim();


    const phone =
        document.getElementById(
            "editPhone"
        ).value.trim();


    if (!/^[0-9]{10}$/.test(phone)) {

        alert(
            "Phone must be exactly 10 digits."
        );

        return;

    }


    try {

        const response =
            await fetch(
                "/api/users/profile",
                {
                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
                        JSON.stringify({

                            name: name,

                            phone: phone

                        })

                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();


            console.error(
                errorText
            );


            alert(
                "Unable to update profile."
            );


            return;

        }


        const updatedProfile =
            await response.json();


        localStorage.setItem(
            "name",
            updatedProfile.name
        );


        localStorage.setItem(
            "email",
            updatedProfile.email
        );


        const userNameElement =
            document.getElementById(
                "userName"
            );


        if (userNameElement) {

            userNameElement.textContent =
                updatedProfile.name;

        }


        alert(
            "Profile updated successfully."
        );


        showProfileSection();


    } catch (error) {

        console.error(
            "Update profile error:",
            error
        );


        alert(
            "Something went wrong while updating profile."
        );

    }

}


// =====================================================
// SHOW CHANGE PASSWORD FORM
// =====================================================

function showChangePasswordSection() {

    const content =
        document.getElementById(
            "content"
        );


    content.innerHTML = `

        <div class="loan-list">

            <h2>
                🔐 Change Password
            </h2>


            <div class="loan-details-card">

                <form id="changePasswordForm">


                    <div class="form-group">

                        <label>
                            Current Password
                        </label>

                        <input
                            type="password"
                            id="currentPassword"
                            placeholder="Enter current password"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>
                            New Password
                        </label>

                        <input
                            type="password"
                            id="newPassword"
                            placeholder="Enter new password"
                            minlength="6"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>
                            Confirm New Password
                        </label>

                        <input
                            type="password"
                            id="confirmPassword"
                            placeholder="Confirm new password"
                            minlength="6"
                            required
                        >

                    </div>


                    <button
                        type="submit"
                        class="primary-btn">

                        🔐 Change Password

                    </button>


                    <button
                        type="button"
                        class="secondary-btn"
                        onclick="showProfileSection()">

                        Cancel

                    </button>


                </form>

            </div>

        </div>

    `;


    document
        .getElementById(
            "changePasswordForm"
        )
        .addEventListener(
            "submit",
            submitChangePassword
        );

}


// =====================================================
// SUBMIT CHANGE PASSWORD
// =====================================================

async function submitChangePassword(
    event
) {

    event.preventDefault();


    const currentPassword =
        document.getElementById(
            "currentPassword"
        ).value;


    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;


    const confirmPassword =
        document.getElementById(
            "confirmPassword"
        ).value;


    if (newPassword.length < 6) {

        alert(
            "New password must be at least 6 characters."
        );

        return;

    }


    if (
        newPassword !==
        confirmPassword
    ) {

        alert(
            "New password and confirm password do not match."
        );

        return;

    }


    try {

        const response =
            await fetch(
                "/api/users/change-password",
                {
                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
                        JSON.stringify({

                            currentPassword:
                                currentPassword,

                            newPassword:
                                newPassword,

                            confirmPassword:
                                confirmPassword

                        })

                }
            );


        const message =
            await response.text();


        if (!response.ok) {

            alert(message);

            return;

        }


        alert(
            "Password changed successfully. Please login again."
        );


        localStorage.removeItem(
            "token"
        );


        window.location.href =
            "/index.html";


    } catch (error) {

        console.error(
            "Change password error:",
            error
        );


        alert(
            "Something went wrong while changing password."
        );

    }

}