import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VerificationResult } from '../models/verification-result';
import { IdentificationResult } from '../models/identification-result';

@Injectable({ providedIn: 'root' })
export class FacialTemplateService {
  private readonly baseUrl = 'http://localhost:8080/api/facial-templates';

  constructor(private http: HttpClient) {}

  verify(formData: FormData): Observable<VerificationResult> {
    return this.http.post<VerificationResult>(`${this.baseUrl}/verify`, formData);
  }

  identify(formData: FormData): Observable<IdentificationResult> {
    return this.http.post<IdentificationResult>(`${this.baseUrl}/identify`, formData);
  }
}
