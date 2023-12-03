import { Component } from '@angular/core';
import { FileHandle } from './../directives/dragDrop.directive';
import { LawsuitService } from 'src/services/lawsuit-service';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { LawsuitDto } from 'src/dto/all-dto';
import { Subscription } from 'rxjs';
import { MatRadioChange } from '@angular/material/radio';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Сервис формирования исковых заявлений';

  isDefendantPhys: boolean = true;

  error?: {message: string; header: string}

  files: FileHandle[] = [];

  lawsuit: LawsuitDto;

  get lawsuitValid(): boolean {
    let result = true;
    Object.values(this.lawsuit).forEach(val => result = result && this.getObjectValid(val))
    return result;
  }

  getObjectValid(obj: any): boolean {
    let result = true;
    if(!obj) return result;
    Object.values(obj).forEach(val => result = result && (val !== undefined && val !== null))
    return result;
  }

  lawsuitSubscription?: Subscription;

  contractSubscription?: Subscription;

  claimSubscription?: Subscription;

  constructor(private lawsuitService: LawsuitService) {
    const now = new Date();
    this.lawsuit = {
      plantiff: {
        INN: '',
        OGRN: '',
        address: '',
        KPP: '',
        paymentAccount: '',
        correspondentAccount: '',
        bank: '',
        BIC: '',
        OKTMO: '',
        representative: '',
      },
      defendant: {
        FIO: '',
        birthdate: now,
        passport: '',
        INN: '',
        OGRN: '',
        address: '',
        title: '',
      },
      area: {
        number: '',
        area: 0,
        address: '',
      },
      contract: {
        date: now,
        period: '',
        paymentPoint: '',
        number: '',
        sum: 0,
        penaltyPoint: '',
      },
      pretension: {
        overduePeriod: '',
        debt: '',       
        peny: 0,       
        penalty: 0,       
        date: now,      
        refuseDate: now,       
        refuseCause: '',       
        unlawRefuseCase: '',       
        due: 0,
      },
      agency: {
        registeringGovermentAgency: '',
        recordNumber: '',
        registryDate: now,
      },
      rules: undefined,
    }
  }

  changeDefendentType(event: MatRadioChange){
    this.isDefendantPhys = event.value == "phys";
  }
  
  fileSize(fileSize: number): string {
    if (fileSize < 1024) return `${fileSize} b`;
    if (fileSize < 1024 * 1024) return `${Math.floor(fileSize / 1024)} Kb`;
    return `${Math.floor(fileSize / (1024 * 1024))} Mb`;
  }

  fileIconPath(file: FileHandle): string {
    const name = file.file.name;
    if(name.endsWith('doc') || name.endsWith('docx'))
      return 'assets/doc.png';
    if(name.endsWith('pdf'))
      return 'assets/pdf.png';
    return 'assets/unknown.png';
  }

  filesDropped(files: any): void {
    this.files = [...this.files, ...files as FileHandle[]];
  }

  clearFiles() {
    this.files = [];
  }

  clearFile(file: FileHandle) {
    let index = this.files.indexOf(file);
    this.files.splice(index, 1);
  }

  uploadFiles(): void {
    const contract = this.files.filter(fileHandle => fileHandle.file.name.toLowerCase().includes('договор')).at(0);
    if(contract){
      const fileToUpload = contract.file;
      const formData = new FormData();
      formData.append('file', fileToUpload, fileToUpload.name);
      this.contractSubscription?.unsubscribe();
      this.contractSubscription = this.lawsuitService.uploadContract(formData).subscribe
      (
          (event: HttpEvent<LawsuitDto>) => {
            if (event.type === HttpEventType.Response) {
              if(event.body)
                this.lawsuit = { ...this.lawsuit, ...event.body }
            }
          },
          (err: HttpErrorResponse) => this.handleError(err)
      );
    }

    const claim = this.files.filter(fileHandle => fileHandle.file.name.toLowerCase().includes('претензия')).at(0);
    if(claim){
      const fileToUpload = claim.file;
      const formData = new FormData();
      formData.append('file', fileToUpload, fileToUpload.name);
      this.claimSubscription?.unsubscribe();
      this.claimSubscription = this.lawsuitService.uploadClaim(formData).subscribe
      (
          (event: HttpEvent<LawsuitDto>) => {
            if (event.type === HttpEventType.Response) {
              if(event.body)
                this.lawsuit = { ...this.lawsuit, ...event.body }
            }
          },
          (err: HttpErrorResponse) => this.handleError(err)
      );
    }
  }

  buildLawsuit(): void {

    this.lawsuit.rules = {
      isRentPeriod: !!this.lawsuit.contract?.period,
      isPeny: !!this.lawsuit.pretension?.peny,
      shouldBeRegistred: !this.lawsuit.pretension?.refuseCause,
      isLegal: !this.isDefendantPhys,
      hasPretentionAnswer: !!this.lawsuit.pretension?.refuseCause,
    }

    this.lawsuitSubscription?.unsubscribe();
    this.lawsuitSubscription = this.lawsuitService.buildLawsuit(this.lawsuit).subscribe
    (
        null, 
        (err: HttpErrorResponse) => this.handleError(err)
    );
  }

  handleError(error: HttpErrorResponse): void {
    this.error = {
      message: error?.message,
      header: "Ошибка " + error?.status,
    }
    setTimeout(()=>{     
      this.error = undefined;
    }, 5000);
  }

  closeError() {
    this.error = undefined;
  }
}
