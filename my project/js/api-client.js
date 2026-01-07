(function(){
  const isFile = window.location.protocol === 'file:';
  const defaultBase = isFile ? 'http://localhost:8080/api' : '/api';
  const base = (window.backendConfig && window.backendConfig.baseUrl) || defaultBase;

  async function post(url, payload){
    let res;
    try {
      res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(payload)
      });
    } catch (err) {
      throw new Error('Network error. Is the backend running at ' + base + '?');
    }
    let body = {};
    try { body = await res.json(); } catch (err) { body = {}; }
    if(!res.ok){ throw new Error(body.error || 'Request failed'); }
    return body;
  }

  const client = {
    auth: {
      register: (phone, password) => post((window.backendConfig && window.backendConfig.auth) || base + '/auth', { phone, password, login: false }),
      login: (phone, password) => post((window.backendConfig && window.backendConfig.auth) || base + '/auth', { phone, password, login: true })
    },
    donors: {
      register: (data) => post((window.backendConfig && window.backendConfig.donors) || base + '/donors', data)
    },
    matches: {
      request: (data) => post((window.backendConfig && window.backendConfig.matches) || base + '/matches', data)
    },
    contact: {
      send: (data) => post((window.backendConfig && window.backendConfig.contact) || base + '/contact', data)
    }
  };

  function formDataToObject(fd){
    const out = {};
    fd.forEach((v, k) => { out[k] = v; });
    return out;
  }

  function bind(selector, handler){
    document.querySelectorAll(selector).forEach(form => {
      form.addEventListener('submit', async function(e){
        e.preventDefault();
        try {
          const data = formDataToObject(new FormData(form));
          await handler(data, form);
        } catch(err){
          alert(err.message || 'Request failed');
        }
      });
    });
  }

  function renderMatches(container, matches){
    if(!container) return;
    container.innerHTML = '';
    if(!matches || !matches.length){
      container.innerHTML = '<p>No donors found yet. Please try again later.</p>';
      return;
    }
    const list = document.createElement('ul');
    list.className = 'match-results';
    matches.forEach(d => {
      const li = document.createElement('li');
      li.textContent = `${d.name} • ${d.bloodType || 'Unknown'} • ${d.pincode || 'N/A'} • ${d.phone}`;
      list.appendChild(li);
    });
    container.appendChild(list);
  }

  document.addEventListener('DOMContentLoaded', function(){
    bind('form[data-api="contact"]', async (data, form) => {
      await client.contact.send({
        name: data.name,
        email: data.email,
        message: data.message
      });
      alert('Message sent');
      form.reset();
    });

    bind('form[data-api="donor"]', async (data, form) => {
      await client.donors.register(data);
      alert('Registration saved. Thank you!');
      form.reset();
      window.location.href = 'index.html';
    });

    bind('form[data-api="match"]', async (data, form) => {
      const resp = await client.matches.request(data);
      const target = document.getElementById('matchResults');
      renderMatches(target, resp.matches || []);
      alert('Request submitted');
      form.reset();
    });
  });

  window.apiClient = client;
})();
