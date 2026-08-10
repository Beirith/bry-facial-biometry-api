import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../models/user';
import {BatchResult} from '../models/batch-result'

@Injectable({providedIn: 'root'})
export class UserService {
  private readonly baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {
  }

  findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  findByCpf(cpf: string): Observable<User> {
    return this.http.get<User>(this.baseUrl, {params: {cpf}});
  }

  getPictureUrl(id: number): string {
    return `${this.baseUrl}/${id}/picture`;
  }

  create(formData: FormData): Observable<User> {
    return this.http.post<User>(this.baseUrl, formData);
  }

  createBatch(formData: FormData): Observable<BatchResult[]> {
    return this.http.post<BatchResult[]>(`${this.baseUrl}/batch`, formData);
  }

  update(id: number, formData: FormData): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, formData);
  }

  updateBatch(formData: FormData): Observable<BatchResult[]> {
    return this.http.put<BatchResult[]>(`${this.baseUrl}/batch`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
