import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { LawsuitDto } from 'src/dto/all-dto';

@Injectable()
export class LawsuitService {
  private readonly url: string = 'http://localhost:8080';

  constructor(@Inject(HttpClient) private http: HttpClient) {}

  buildLawsuit(lawsuit: LawsuitDto) {
    const endpoint = `${this.url}/api/v1/lawsuit`;
    return this.http.post(endpoint, lawsuit, { headers: { Accept: "application/octet-stream" }, responseType: "blob"})
        .pipe(
            map(report => {
            const a = document.createElement('a');
            document.body.appendChild(a);
            const blob: any = new Blob([report], { type: 'octet/stream' });
            const url = window.URL.createObjectURL(blob);
            a.href = url;
            a.download = 'Исковое заявление.docx';
            a.click();
            window.URL.revokeObjectURL(url);
        })
      );
  }

  uploadContract(formData: FormData): Observable<HttpEvent<LawsuitDto>> {
    const endpoint = `${this.url}/api/v1/contract`;
    return this.http.post<LawsuitDto>(endpoint, formData, {reportProgress: true, observe: 'events'})
  }

  uploadClaim(formData: FormData): Observable<HttpEvent<LawsuitDto>> {
    const endpoint = `${this.url}/api/v1/claim`;
    return this.http.post<LawsuitDto>(endpoint, formData, {reportProgress: true, observe: 'events'})
  }
}
