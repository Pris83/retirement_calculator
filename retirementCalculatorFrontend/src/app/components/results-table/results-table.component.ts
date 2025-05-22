import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-results-table',
  standalone: true,
  imports: [MatTableModule,CommonModule],
  templateUrl: './results-table.component.html',
  styleUrls: ['./results-table.component.scss']
})
export class ResultsTableComponent implements OnChanges {
  @Input() resultData: any;
  displayedColumns: string[] = ['currentAge', 'retirementAge', 'interestRate', 'lifestyleType', 'monthlyDeposit', 'futureValue'];
  dataSource: any[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['resultData']) {
      this.dataSource = this.resultData ;
      console.log('Result data received by table:', this.dataSource);
    }
  }
}
