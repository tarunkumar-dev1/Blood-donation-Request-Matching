document.addEventListener('DOMContentLoaded', function(){
	// Toggle mobile navigation
	(function () {
		const navToggle = document.getElementById('nav-toggle');
		const mainNav = document.getElementById('main-nav');
		if (navToggle && mainNav) {
			navToggle.addEventListener('click', function () {
				mainNav.classList.toggle('open');
			});
		}
		// Set current year in footer
		const yearEl = document.getElementById('year');
		if (yearEl) {
			yearEl.textContent = new Date().getFullYear();
		}
	})();

	// Smooth scroll for internal links
	document.querySelectorAll('a[href^="#"]').forEach(a=>{
		a.addEventListener('click', e=>{
			const target = document.querySelector(a.getAttribute('href'));
			if(target){ e.preventDefault(); target.scrollIntoView({behavior:'smooth', block:'start'}); if(mainNav.classList.contains('open')) mainNav.classList.remove('open'); }
		})
	});

	// Load and update statistics from database
	loadStatistics();
	// Refresh statistics every 30 seconds
	setInterval(loadStatistics, 30000);
});

// Function to load statistics from backend
async function loadStatistics() {
	try {
		const response = await fetch('http://localhost:8080/api/stats');
		if (!response.ok) {
			console.error('Failed to fetch statistics');
			return;
		}
		
		const data = await response.json();
		
		if (data.success) {
			// Update blood availability cards
			updateBloodAvailability(data.bloodAvailability || {});
			
			// Update statistics cards
			updateStatCard('Total Donors', data.totalDonors || 0, `Units available: ${data.totalDonors || 0}`);
			updateStatCard('Lives Saved', data.livesSaved || 0, 'Since launch');
			updateStatCard('Active Requests', data.activeRequests || 0, 'Open requests');
			updateStatCard('Successful Matches', data.successfulMatches || 0, 'Completed matches');
			
			console.log('Statistics updated successfully');
		}
	} catch (error) {
		console.error('Error loading statistics:', error);
	}
}

// Update blood availability cards
function updateBloodAvailability(bloodData) {
	const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
	const groupCards = document.querySelectorAll('.group-card');
	
	groupCards.forEach(card => {
		const groupName = card.querySelector('.group-name');
		if (groupName) {
			const bloodType = groupName.textContent.trim();
			const count = bloodData[bloodType] || 0;
			const unitCount = card.querySelector('.unit-count');
			if (unitCount) {
				// Animate the number change
				animateNumber(unitCount, parseInt(unitCount.textContent) || 0, count);
			}
		}
	});
}

// Update individual stat card
function updateStatCard(label, value, meta) {
	const statCards = document.querySelectorAll('.stat-card');
	statCards.forEach(card => {
		const cardLabel = card.querySelector('.stat-label');
		if (cardLabel && cardLabel.textContent.trim() === label) {
			const valueEl = card.querySelector('.stat-value');
			if (valueEl) {
				const currentValue = parseInt(valueEl.textContent) || 0;
				animateNumber(valueEl, currentValue, value);
			}
			const metaEl = card.querySelector('.stat-meta');
			if (metaEl && meta) {
				metaEl.textContent = meta;
			}
		}
	});
}

// Animate number changes
function animateNumber(element, from, to) {
	if (from === to) return;
	
	const duration = 1000; // 1 second
	const steps = 30;
	const increment = (to - from) / steps;
	let current = from;
	let step = 0;
	
	const timer = setInterval(() => {
		step++;
		current += increment;
		
		if (step >= steps) {
			element.textContent = to;
			clearInterval(timer);
		} else {
			element.textContent = Math.round(current);
		}
	}, duration / steps);
}
