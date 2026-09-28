package com.example.loanmanagement.dto;

public class AdminDashboardResponse {

    private long totalCustomers;
    private long totalLoanOfficers;
    private long totalLoans;
    private long appliedLoans;
    private long approvedLoans;
    private long rejectedLoans;
    private long activeLoans;
    private long closedLoans;

    public AdminDashboardResponse() {
    }

    public AdminDashboardResponse(
            long totalCustomers,
            long totalLoanOfficers,
            long totalLoans,
            long appliedLoans,
            long approvedLoans,
            long rejectedLoans,
            long activeLoans,
            long closedLoans) {

        this.totalCustomers = totalCustomers;
        this.totalLoanOfficers = totalLoanOfficers;
        this.totalLoans = totalLoans;
        this.appliedLoans = appliedLoans;
        this.approvedLoans = approvedLoans;
        this.rejectedLoans = rejectedLoans;
        this.activeLoans = activeLoans;
        this.closedLoans = closedLoans;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalLoanOfficers() {
        return totalLoanOfficers;
    }

    public long getTotalLoans() {
        return totalLoans;
    }

    public long getAppliedLoans() {
        return appliedLoans;
    }

    public long getApprovedLoans() {
        return approvedLoans;
    }

    public long getRejectedLoans() {
        return rejectedLoans;
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public long getClosedLoans() {
        return closedLoans;
    }
}