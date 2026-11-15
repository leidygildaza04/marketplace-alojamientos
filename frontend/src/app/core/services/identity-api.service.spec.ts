import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IdentityApiService } from './identity-api.service';

describe('IdentityApiService', () => {
  let service: IdentityApiService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(IdentityApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('posts email and password to the Identity login endpoint as JSON', () => {
    let result: unknown;
    service.login('guest@example.com', 'secret').subscribe((response) => result = response);

    const request = httpTesting.expectOne('/identity-api/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.headers.get('Content-Type')).toBe('application/json');
    expect(request.request.body).toEqual({ email: 'guest@example.com', password: 'secret' });

    const response = { token: 'test-token', role: 'GUEST' as const, expiresInMinutes: 60 };
    request.flush(response);
    expect(result).toEqual(response);
  });
});
