import {TestBed} from '@angular/core/testing';
import {HTTP_INTERCEPTORS, HttpClient, provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting, HttpTestingController} from '@angular/common/http/testing';
import {JwtInterceptor} from './jwt.interceptor';
import {AuthService} from '../services/auth.service';

describe('JwtInterceptor', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let httpTestingController: HttpTestingController;
  let httpClient: HttpClient;
  const FAKE_JWT = 'fake.jwt.token';

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),

        provideHttpClientTesting(),

        {
          provide: HTTP_INTERCEPTORS,
          useClass: JwtInterceptor,
          multi: true
        },

        {provide: AuthService, useValue: authServiceSpy}
      ]
    });

    authServiceMock = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(httpClient).toBeTruthy();
  });

  it('should add an Authorization header when a token is present', () => {
    authServiceMock.getToken.and.returnValue(FAKE_JWT);
    httpClient.get('/api/test').subscribe();
    const req = httpTestingController.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${FAKE_JWT}`);
    req.flush({});
  });

  it('should not add an Authorization header when no token is present', () => {
    authServiceMock.getToken.and.returnValue(null);
    httpClient.get('/api/test').subscribe();
    const req = httpTestingController.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });
});
