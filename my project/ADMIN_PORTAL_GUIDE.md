# 🩸 Admin Portal - Blood Donation System

## Overview
The Admin Portal provides intelligent matching between blood donors and requests with real-time notifications and management capabilities.

## Features

### ✨ Intelligent Matching System
- **Automatic Matching**: When a donor registers or a blood request is made, the system automatically finds matching parties
- **Priority-Based Sorting**: Matches are prioritized by:
  - Location proximity (same pincode = HIGH priority)
  - Urgency keywords in requests ("urgent", "emergency" = HIGH priority)
  - Blood type compatibility

### 📊 Real-Time Dashboard
- Total matches count
- Pending matches awaiting approval
- Successful completed matches
- Available donors count
- Auto-refreshes every 15 seconds

### 🔍 Match Management
**Match Cards Display:**
- Donor information (name, phone, email)
- Request information (patient name, email)
- Blood type required
- Location matching status
- Priority level (high/medium/low)
- Request notes
- Timestamp

**Status Workflow:**
1. **NEW** - Just created, awaiting admin review
2. **PENDING** - Approved and parties notified
3. **COMPLETED** - Blood donation completed

### 🎯 Admin Actions
- **Approve Match**: Move to pending and notify both parties
- **Contact Both**: Send communication to donor and requester
- **Mark Complete**: Finalize successful blood donation
- **Reject**: Remove incompatible matches
- **Filter**: View by status (All/New/Pending/Completed)

## Access URL
```
http://localhost:8080/admin.html
```

## How It Works

### When a Donor Registers:
1. Donor submits registration form
2. System saves donor to database
3. System automatically searches for matching blood requests
4. Matches appear in admin portal instantly
5. Admin receives notification of new matches

### When a Blood Request is Made:
1. Patient/Hospital submits blood request
2. System saves request to database
3. System automatically searches for matching donors
4. Matches appear in admin portal instantly
5. Admin can approve and coordinate donation

### Match Priority Algorithm:
```
HIGH Priority:
- Same pincode/location as requester
- Request contains "urgent" or "emergency"

MEDIUM Priority:
- Blood type matches but different location
- Standard requests

LOW Priority:
- Optional matches for future reference
```

## API Endpoints

### Get All Matches
```
GET /api/admin/matches
```

**Response:**
```json
{
  "matches": [
    {
      "matchId": "M-ABC12345",
      "donorName": "John Doe",
      "donorPhone": "+91-9876543210",
      "donorEmail": "john@example.com",
      "requestName": "Hospital XYZ",
      "requestEmail": "hospital@example.com",
      "bloodType": "O+",
      "locationMatch": true,
      "requestNote": "Urgent - Surgery tomorrow",
      "status": "new",
      "priority": "high",
      "matchedAt": "2025-12-19T23:00:00"
    }
  ],
  "availableDonors": 15
}
```

### Approve Match
```
POST /api/admin/matches/approve
Body: matchId=M-ABC12345
```

### Complete Match
```
POST /api/admin/matches/complete
Body: matchId=M-ABC12345
```

## Database Integration

The system queries these tables:
- `donors` - All registered blood donors
- `match_requests` - All blood requests
- Dynamically creates matches by comparing blood types and locations

## Workflow Example

**Scenario: Emergency Blood Request**

1. Hospital submits request:
   - Blood Type: A+
   - Location: Pincode 110001
   - Note: "Urgent - Emergency surgery"

2. System automatically:
   - Searches all A+ donors
   - Prioritizes donors in pincode 110001
   - Creates match records
   - Shows in admin portal

3. Admin sees match card:
   - **Status**: NEW
   - **Priority**: HIGH (urgent + same location)
   - Donor: "Ram Kumar, +91-9988776655"
   - Request: "AIIMS Hospital"

4. Admin actions:
   - Clicks "Approve Match"
   - Both parties get notified
   - Status changes to PENDING

5. After donation:
   - Admin clicks "Mark Complete"
   - Match recorded as COMPLETED
   - Statistics updated

## Auto-Refresh
- Dashboard auto-refreshes every 15 seconds
- Manual refresh button available
- Real-time updates for new matches

## Statistics Tracking
- **Total Matches**: All time matches created
- **Pending**: Currently in progress
- **Successful**: Completed donations
- **Available Donors**: Current donor pool

## Benefits

✅ **Fast Response**: Instant matching when donors/requests added  
✅ **Smart Prioritization**: Critical requests get immediate attention  
✅ **Centralized Management**: Single portal for all matches  
✅ **Audit Trail**: Complete history of all matches  
✅ **Real-time Updates**: Always current information  
✅ **Location-Aware**: Prioritizes nearby donors  

## Future Enhancements
- SMS/Email notifications
- Donation scheduling
- Donor availability calendar
- Blood bank integration
- Mobile app support
- Analytics and reporting

---

**Admin Portal makes blood donation coordination efficient, fast, and life-saving! 🩸**
