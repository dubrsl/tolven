insert into core.tolvenauth
(policy, urlMethod, contextPath, url, filters) values
('default', 'GET', '/gatekeeper/rs', '/**', 'tssl,rsauthz'),
('default', 'POST', '/gatekeeper/rs', '/**', 'tssl,rsauthz'),

('default', 'GET', '/gatekeeper/html', '/**', 'tssl,tauthc'),
('default', 'POST', '/gatekeeper/html', '/**', 'tssl,tauthc'),
('default', 'GET', '/gatekeeper/html', '/index.jsp', ''),
('default', 'GET', '/gatekeeper/html', '/styles/**', ''),
('default', 'GET', '/gatekeeper/html', '/images/**', ''),
('default', 'GET', '/gatekeeper/html', '/templates/**', ''),
('default', 'GET', '/gatekeeper/html', '/public/**', ''),
('default', 'GET', '/gatekeeper/html', '/recoverloginpassword/**', ''),
('default', 'POST', '/gatekeeper/html', '/recoverloginpassword/**', ''),

('default', 'GET', '/gatekeeper/ws', '/**', 'tssl,wsauthz'),
('default', 'POST', '/gatekeeper/ws', '/**', 'tssl,wsauthz'),

('default', 'GET', '/api', '/**', 'tssl,rspreauthz'),
('default', 'POST', '/api', '/**', 'tssl,rspreauthz'),

('default', 'GET', '/Tolven', '/**', 'tssl,preauthc'),
('default', 'POST', '/Tolven', '/**', 'tssl,preauthc'),
('default', 'GET', '/Tolven', '/public/**', ''),

('default', 'GET', '/ws', '/**', 'tssl,wspreauthz'),
('default', 'POST', '/ws', '/**', 'tssl,wspreauthz')