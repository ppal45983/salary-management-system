export default async function handler(req, res) {
  const { path, ...query } = req.query;
  const pathStr = Array.isArray(path) ? path.join('/') : (path || '');
  
  const searchParams = new URLSearchParams();
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== null) {
      if (Array.isArray(value)) {
        value.forEach(v => searchParams.append(key, v));
      } else {
        searchParams.append(key, value);
      }
    }
  }
  const queryString = searchParams.toString();
  const targetUrl = `http://13.204.76.101:8080/api/v1/${pathStr}${queryString ? `?${queryString}` : ''}`;

  // Enable CORS
  res.setHeader('Access-Control-Allow-Credentials', 'true');
  res.setHeader('Access-Control-Allow-Origin', req.headers.origin || '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,OPTIONS,PATCH,DELETE,POST,PUT');
  res.setHeader(
    'Access-Control-Allow-Headers',
    'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version, Authorization'
  );

  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  try {
    const headers = { ...req.headers };
    delete headers.host;
    delete headers.connection;
    delete headers['content-length'];

    const fetchOptions = {
      method: req.method,
      headers: {
        ...headers,
        'Content-Type': req.headers['content-type'] || 'application/json',
      }
    };

    if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(req.method) && req.body) {
      fetchOptions.body = typeof req.body === 'string' ? req.body : JSON.stringify(req.body);
    }

    const response = await fetch(targetUrl, fetchOptions);
    const contentType = response.headers.get('content-type') || '';
    
    res.status(response.status);

    const disposition = response.headers.get('content-disposition');
    if (disposition) {
      res.setHeader('content-disposition', disposition);
    }

    if (contentType.includes('application/json')) {
      const data = await response.json();
      return res.json(data);
    } else {
      const text = await response.text();
      return res.send(text);
    }
  } catch (error) {
    console.error('API Proxy Error:', error);
    return res.status(502).json({
      success: false,
      message: 'Backend server connection error: ' + error.message
    });
  }
}